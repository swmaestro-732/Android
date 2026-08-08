package com.chillsam.courmy.course.data.draft

import com.chillsam.courmy.course.entity.CourseDraftVO
import kotlinx.serialization.Serializable

/**
 * 임시저장 초안 1건의 저장 형태. 목록 표시용 메타([savedAtMillis]·[title])와 전체 내용([content]).
 *
 * 편집 세션 id([id])로 upsert 하므로 제목을 바꿔 저장해도 중복이 생기지 않는다.
 */
@Serializable
data class StoredDraft(
    val id: String,
    val savedAtMillis: Long,
    val title: String,
    val content: CourseDraftVO,
)

/**
 * 초안을 기기에 보관하는 저장소.
 *
 * 인터페이스로 둔 이유: 구현은 Android DataStore 라 Context 가 필요한데, 초안 로직 단위 테스트는
 * 순수 JVM 이라 Context 를 만들 수 없다. 테스트는 메모리 구현을 끼워 넣는다.
 */
interface DraftLocalStore {
    suspend fun load(): List<StoredDraft>

    suspend fun save(drafts: List<StoredDraft>)
}
