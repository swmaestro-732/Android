package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseCreate.CourseCreateDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.toCreateRequest
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.toVO
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CourseVisibility
import com.chillsam.courmy.course.entity.DraftSummaryVO
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 코스 작성·보관 Repository 구현.
 *
 * 세션 목록·완성 코스는 프로세스가 살아있는 동안(= 앱이 켜져 있는 동안)만 인메모리로 보관하며,
 * 콜드 스타트 시 초기화된다. [CourseDataModule] 이 `@Singleton` 으로 제공하므로 앱 전역에서 한 인스턴스를 공유한다.
 *
 * TODO-API-SPEC: 현재는 UI 확인용 더미다. "새 코스 만들기"는 빈 초안에서 시작하며 추천 태그만 채워 준다(정적 제안).
 * 실제 임시저장/코스 조회·저장 API 가 붙으면 DataSource·DTO 를 추가하고 인메모리 상태를 교체한다.
 */
class CourseRepositoryImpl(
    private val courseDetailDataSource: CourseDetailDataSource,
    private val courseCreateDataSource: CourseCreateDataSource,
    /**
     * 현재 사용자 id 제공자("내 코스" 판정용).
     * TokenStore 를 직접 받으면 Android 암호화 저장에 묶여 단위 테스트에서 생성할 수 없어 함수로 받는다.
     */
    private val myUserId: () -> Long?,
) : CourseRepository {
    private val _savedCourses = MutableStateFlow<List<SavedCourseVO>>(emptyList())
    override val savedCourses: StateFlow<List<SavedCourseVO>> = _savedCourses.asStateFlow()

    private val _drafts = MutableStateFlow<List<DraftSummaryVO>>(emptyList())
    override val drafts: StateFlow<List<DraftSummaryVO>> = _drafts.asStateFlow()

    private val _lastCompleted = MutableStateFlow<CourseCompleteVO?>(null)
    override val lastCompleted: StateFlow<CourseCompleteVO?> = _lastCompleted.asStateFlow()

    /** id → 저장 시각·전체 초안. 편집 세션 id 를 키로 upsert 하므로 제목을 바꿔도 중복이 생기지 않는다. */
    private val draftsById = LinkedHashMap<String, StoredDraft>()

    /** 다음 [getCourseDraft] 가 이어서 편집할 초안 id. 한 번 소비하면 비운다. */
    private var pendingEditId: String? = null

    /** 현재 편집 세션의 초안 id. 저장 시 이 id 로 upsert 한다(제목 무관). */
    private var editingId: String? = null

    /** 새 초안 세션 id 발급용 카운터(랜덤/UUID 없이 안정적으로). */
    private var draftSeq = 0

    override suspend fun getCourseDraft(): CourseDraftVO {
        val id = pendingEditId
        pendingEditId = null
        // 이어서 편집이면 그 초안 id 로, 새 코스면 새 세션 id 로 편집 세션을 연다.
        editingId = id ?: newDraftId()
        return id?.let { draftsById[it]?.content } ?: EMPTY_DRAFT
    }

    override fun beginEditDraft(draftId: String) {
        pendingEditId = draftId
    }

    override fun saveDraft(draft: CourseDraftVO) {
        // 현재 편집 세션 id 로 upsert. 같은 세션에서 제목을 바꿔 다시 저장해도 같은 초안을 덮어쓴다.
        // 세션 id 는 새 코스/이어서 편집 진입 때 getCourseDraft 가 새로 발급하므로, 다른 코스와 섞이지 않는다.
        val id = editingId ?: newDraftId()
        editingId = id
        val title = draft.name.ifBlank { DEFAULT_DRAFT_TITLE }
        draftsById[id] = StoredDraft(id, System.currentTimeMillis(), title, draft)
        _drafts.value = draftsById.values.map { DraftSummaryVO(it.id, it.title, it.savedAtMillis) }
    }

    private fun newDraftId(): String {
        draftSeq += 1
        return "draft-$draftSeq"
    }

    override fun completeCourse(course: CourseCompleteVO?) {
        _lastCompleted.value = course
        course?.let { completed ->
            _savedCourses.update { it + SavedCourseVO(completed.title, System.currentTimeMillis()) }
        }
    }

    override suspend fun getCourseDetail(courseId: Long): CourseDetailVO {
        val envelope = courseDetailDataSource.getCourseDetail(courseId)
        val data = requireNotNull(envelope.data) { "코스 상세 응답에 data 가 없습니다: courseId=$courseId" }
        return data.toVO(myUserId = myUserId())
    }

    /** 임시저장 1건: id + 저장 시각 + 표시 제목 + 전체 초안 내용. */
    private data class StoredDraft(
        val id: String,
        val savedAtMillis: Long,
        val title: String,
        val content: CourseDraftVO,
    )

    private companion object {
        /** 이름 없이 저장한 초안의 목록 표시용 기본 제목. */
        const val DEFAULT_DRAFT_TITLE = "제목 없는 코스"

        val EMPTY_DRAFT =
            CourseDraftVO(
                name = "",
                description = "",
                tags = emptyList(),
                suggestedTags = listOf("감성카페", "통창뷰", "조용한", "데이트"),
                places = emptyList(),
                visibility = CourseVisibility.PUBLIC,
            )
    }

    override suspend fun createCourse(
        draft: CourseDraftVO,
        thumbnailUrl: String?,
        published: Boolean,
    ): Long {
        val envelope = courseCreateDataSource.createCourse(draft.toCreateRequest(thumbnailUrl, published))
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "코스 생성 응답에 data 가 없습니다."
            }
        return requireNotNull(data.courseId) { "코스 생성 응답에 courseId 가 없습니다." }
    }
}
