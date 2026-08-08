package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseCreate.CourseCreateDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.toCreateRequest
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.toVO
import com.chillsam.courmy.course.data.draft.DraftLocalStore
import com.chillsam.courmy.course.data.draft.DraftManager
import com.chillsam.courmy.course.data.follow.FollowDataSource
import com.chillsam.courmy.course.data.recommendedTag.RecommendedTagDataSource
import com.chillsam.courmy.course.data.recommendedTag.dto.toTagList
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CourseVisibility
import com.chillsam.courmy.course.entity.DraftSummaryVO
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
    private val recommendedTagDataSource: RecommendedTagDataSource,
    private val draftLocalStore: DraftLocalStore,
    private val followDataSource: FollowDataSource,
    /**
     * 현재 사용자 id 제공자("내 코스" 판정용).
     * TokenStore 를 직접 받으면 Android 암호화 저장에 묶여 단위 테스트에서 생성할 수 없어 함수로 받는다.
     */
    private val myUserId: () -> Long?,
) : CourseRepository {
    private val _savedCourses = MutableStateFlow<List<SavedCourseVO>>(emptyList())
    override val savedCourses: StateFlow<List<SavedCourseVO>> = _savedCourses.asStateFlow()

    private val draftManager = DraftManager(draftLocalStore)

    override val drafts: StateFlow<List<DraftSummaryVO>> = draftManager.drafts

    private val _lastCompleted = MutableStateFlow<CourseCompleteVO?>(null)
    override val lastCompleted: StateFlow<CourseCompleteVO?> = _lastCompleted.asStateFlow()

    override suspend fun loadDrafts() = draftManager.load()

    override suspend fun getCourseDraft(): CourseDraftVO = draftManager.beginSession()

    override fun beginEditDraft(draftId: String) = draftManager.beginEdit(draftId)

    override fun saveDraft(draft: CourseDraftVO) = draftManager.save(draft)

    override fun deleteDraft(draftId: String) = draftManager.delete(draftId)

    override fun completeCourse(course: CourseCompleteVO?) {
        _lastCompleted.value = course
        course?.let { completed ->
            _savedCourses.update { it + SavedCourseVO(completed.title, System.currentTimeMillis()) }
        }
    }

    override suspend fun getRecommendedTags(
        placeIds: List<Long>,
        limit: Int,
    ): List<String> {
        val envelope = recommendedTagDataSource.getRecommendedTags(placeIds, limit)
        return envelope.data?.toTagList().orEmpty()
    }

    override suspend fun setFollowAuthor(
        userId: Long,
        follow: Boolean,
    ): Boolean {
        val envelope = followDataSource.setFollow(userId, follow)
        // 응답에 data 가 없으면 요청한 상태가 반영된 것으로 본다(2xx 를 받았으므로).
        return envelope.data?.isFollowing ?: follow
    }

    override suspend fun getCourseDetail(courseId: Long): CourseDetailVO {
        val envelope = courseDetailDataSource.getCourseDetail(courseId)
        val data = requireNotNull(envelope.data) { "코스 상세 응답에 data 가 없습니다: courseId=$courseId" }
        return data.toVO(myUserId = myUserId())
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
