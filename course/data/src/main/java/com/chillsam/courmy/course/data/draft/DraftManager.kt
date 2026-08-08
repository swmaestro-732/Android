package com.chillsam.courmy.course.data.draft

import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.DraftSummaryVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 임시저장 초안의 편집 세션과 보관을 담당한다.
 *
 * 메모리를 정본으로 두고 화면에 즉시 반영한 뒤, 기기 쓰기는 뒤에서 따라간다.
 * 편집 세션 id 로 upsert 하므로 제목을 바꿔 저장해도 같은 초안이 갱신될 뿐 중복이 생기지 않는다.
 */
class DraftManager(
    private val localStore: DraftLocalStore,
) {
    private val _drafts = MutableStateFlow<List<DraftSummaryVO>>(emptyList())
    val drafts: StateFlow<List<DraftSummaryVO>> = _drafts.asStateFlow()

    private val byId = LinkedHashMap<String, StoredDraft>()

    /** 다음 [beginSession] 이 이어서 편집할 초안 id. 한 번 소비하면 비운다. */
    private var pendingEditId: String? = null

    /** 현재 편집 세션의 초안 id. 저장 시 이 id 로 upsert 한다(제목 무관). */
    private var editingId: String? = null

    /** 새 초안 세션 id 발급용 카운터(랜덤/UUID 없이 안정적으로). */
    private var seq = 0

    /**
     * 초안을 기기에 쓰는 전용 스코프.
     *
     * ViewModel 스코프를 쓰면 안 된다 — "임시저장하고 나가기"는 저장 직후 화면을 닫아 ViewModel 이
     * 정리되므로, 그 스코프에 걸린 쓰기는 취소돼 초안이 사라진다.
     */
    private val persistScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** 읽어오는 중에 저장이 끼어들어 서로 덮어쓰지 않도록 하는 잠금. */
    private val lock = Mutex()
    private var hydrated = false

    /** 기기에 남은 초안을 한 번만 읽어 메모리로 올린다. */
    suspend fun load() {
        lock.withLock {
            if (hydrated) return
            hydrated = true
            val stored = runCatching { localStore.load() }.getOrDefault(emptyList())
            stored.forEach { byId[it.id] = it }
            // 새 세션 id 가 저장된 초안과 겹치지 않도록 남아 있는 번호 뒤에서 이어 발급한다.
            seq = stored.mapNotNull { it.id.removePrefix(ID_PREFIX).toIntOrNull() }.maxOrNull() ?: 0
            publish()
        }
    }

    /**
     * 편집 세션을 연다. [beginEdit] 로 지정된 초안이 있으면 그 내용을, 아니면 빈 초안을 돌려준다.
     */
    suspend fun beginSession(): CourseDraftVO {
        load()
        val id = pendingEditId
        pendingEditId = null
        editingId = id ?: newId()
        return id?.let { byId[it]?.content } ?: CourseDraftVO.empty
    }

    fun beginEdit(draftId: String) {
        pendingEditId = draftId
    }

    fun save(draft: CourseDraftVO) {
        val id = editingId ?: newId()
        editingId = id
        val title = draft.name.ifBlank { DEFAULT_TITLE }
        byId[id] = StoredDraft(id, System.currentTimeMillis(), title, draft)
        publish()
        // 화면에는 즉시 반영하고, 기기 쓰기는 뒤에서 이어서 한다.
        persistScope.launch {
            lock.withLock { runCatching { localStore.save(byId.values.toList()) } }
        }
    }

    /**
     * 초안 1건을 지운다.
     *
     * 지금 편집 중인 초안을 지웠다면 세션 id 도 끊는다. 안 그러면 이어서 임시저장할 때 같은 id 로
     * 되살아난다.
     */
    fun delete(draftId: String) {
        if (byId.remove(draftId) == null) return
        if (editingId == draftId) editingId = null
        if (pendingEditId == draftId) pendingEditId = null
        publish()
        persistScope.launch {
            lock.withLock { runCatching { localStore.save(byId.values.toList()) } }
        }
    }

    private fun publish() {
        _drafts.value = byId.values.map { DraftSummaryVO(it.id, it.title, it.savedAtMillis) }
    }

    private fun newId(): String {
        seq += 1
        return "$ID_PREFIX$seq"
    }

    private companion object {
        /** 초안 세션 id 접두사. 저장된 id 에서 번호를 되읽을 때도 쓴다. */
        const val ID_PREFIX = "draft-"

        /** 이름 없이 저장한 초안의 목록 표시용 기본 제목. */
        const val DEFAULT_TITLE = "제목 없는 코스"
    }
}
