package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseCreate.CourseCreateDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.toCreateRequest
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.toVO
import com.chillsam.courmy.course.data.courseManage.CourseManageDataSource
import com.chillsam.courmy.course.data.draft.DraftDataSource
import com.chillsam.courmy.course.data.draft.dto.toDraftUpdateRequest
import com.chillsam.courmy.course.data.draft.dto.toDraftVO
import com.chillsam.courmy.course.data.draft.dto.toVOList
import com.chillsam.courmy.course.data.follow.FollowDataSource
import com.chillsam.courmy.course.data.recommendedTag.RecommendedTagDataSource
import com.chillsam.courmy.course.data.recommendedTag.dto.toTagList
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.DraftSummaryVO
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 코스 작성·보관 Repository 구현.
 *
 * 저장 완료 목록·완성 코스는 프로세스가 살아있는 동안(= 앱이 켜져 있는 동안)만 인메모리로 보관하며,
 * 콜드 스타트 시 초기화된다. [CourseDataModule] 이 `@Singleton` 으로 제공하므로 앱 전역에서 한 인스턴스를 공유한다.
 *
 * 임시저장 초안은 기기가 아니라 서버에 발행 전 코스로 남으므로, 초안 전용 API 는 목록 조회 하나뿐이고
 * 나머지(생성·갱신·삭제·조회)는 코스 API 를 그대로 쓴다.
 */
class CourseRepositoryImpl(
    private val courseDetailDataSource: CourseDetailDataSource,
    private val courseCreateDataSource: CourseCreateDataSource,
    private val recommendedTagDataSource: RecommendedTagDataSource,
    private val draftDataSource: DraftDataSource,
    /** 초안 갱신(`PATCH`)·삭제(`DELETE`)·공개 설정 조회용. 초안도 발행 전 코스라 코스 관리 API 를 쓴다. */
    private val courseManageDataSource: CourseManageDataSource,
    private val followDataSource: FollowDataSource,
    /**
     * 현재 사용자 id 제공자("내 코스" 판정용).
     * TokenStore 를 직접 받으면 Android 암호화 저장에 묶여 단위 테스트에서 생성할 수 없어 함수로 받는다.
     */
    private val myUserId: () -> Long?,
) : CourseRepository {
    private val _savedCourses = MutableStateFlow<List<SavedCourseVO>>(emptyList())
    override val savedCourses: StateFlow<List<SavedCourseVO>> = _savedCourses.asStateFlow()

    private val _lastCompleted = MutableStateFlow<CourseCompleteVO?>(null)
    override val lastCompleted: StateFlow<CourseCompleteVO?> = _lastCompleted.asStateFlow()

    override suspend fun getDrafts(): List<DraftSummaryVO> = draftDataSource.getDrafts().toVOList()

    override suspend fun getCourseDraft(courseId: Long): CourseDraftVO {
        val envelope = courseDetailDataSource.getCourseDetail(courseId)
        val data = requireNotNull(envelope.data) { "임시저장 코스 응답에 data 가 없습니다: courseId=$courseId" }
        // 공개 설정은 화면 조합 응답에 없어 도메인 API 에서 따로 읽는다(편집 화면과 같은 이유).
        // 못 읽어도 이어서 작성 자체는 되어야 하므로 막지 않고 기본값으로 둔다.
        val visibility = runCatching { courseManageDataSource.getVisibility(courseId) }.getOrNull()
        return data.toDraftVO(visibility)
    }

    override suspend fun saveDraft(
        draft: CourseDraftVO,
        courseId: Long?,
    ): Long {
        // 이미 만들어 둔 초안이면 갱신한다. 다시 POST 하면 임시저장을 누른 횟수만큼 초안이 복제된다.
        if (courseId != null) {
            courseManageDataSource.updateCourse(courseId, draft.toDraftUpdateRequest())
            return courseId
        }
        return createCourse(draft, thumbnailUrl = draft.thumbnailUrl.ifBlank { null }, published = false)
    }

    override suspend fun deleteDraft(courseId: Long) = courseManageDataSource.deleteCourse(courseId)

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
