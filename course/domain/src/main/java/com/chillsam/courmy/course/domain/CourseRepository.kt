package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.DraftSummaryVO
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.StateFlow

/**
 * 코스 작성·보관 데이터 접근 규약. 구현은 data 레이어.
 *
 * 세션 목록([savedCourses])과 마지막 완성 코스([lastCompleted])는 앱이 켜져 있는 동안 공유되는
 * 인메모리 상태라 [StateFlow] 로 노출하며, 콜드 스타트 시 초기화된다.
 *
 * 임시저장 초안은 서버에 발행 전(`isPublished = false`) 코스로 보관된다. 기기에 두지 않으므로
 * 조회·저장·삭제 모두 왕복이 필요한 suspend 함수다.
 */
interface CourseRepository {
    /** 저장 완료한 코스 목록(마이 화면 노출). */
    val savedCourses: StateFlow<List<SavedCourseVO>>

    /** 방금 저장 완료한 코스(FS-34-Done 완성 화면 표시용). null 이면 완성 화면이 예시로 폴백. */
    val lastCompleted: StateFlow<CourseCompleteVO?>

    /** 내 임시저장 목록(`GET /api/v1/courses/drafts`). 서버가 전체를 배열로 주므로 커서 페이징이 없다. */
    suspend fun getDrafts(): List<DraftSummaryVO>

    /**
     * 임시저장 초안의 전체 내용을 불러온다(이어서 작성).
     * 초안을 되읽는 경로는 코스 조회뿐이라 발행된 코스와 같은 API 를 쓴다.
     */
    suspend fun getCourseDraft(courseId: Long): CourseDraftVO

    /**
     * 작성 중인 초안을 서버에 임시저장하고 코스 id 를 돌려준다.
     *
     * [courseId] 가 있으면 그 초안을 갱신(`PATCH`)하고, 없으면 새로 만든다(`POST`).
     * 이미 만든 초안에 다시 `POST` 하면 같은 코스가 초안 목록에 복제되므로 반드시 구분해서 부른다.
     */
    suspend fun saveDraft(
        draft: CourseDraftVO,
        courseId: Long?,
    ): Long

    /** 임시저장 초안 1건을 지운다(발행 전 코스라 코스 삭제와 같은 엔드포인트를 쓴다). */
    suspend fun deleteDraft(courseId: Long)

    /** 코스 저장 완료 처리: 완성 화면 데이터를 보관하고 [savedCourses] 에도 추가한다. */
    fun completeCourse(course: CourseCompleteVO?)

    /**
     * 코스에 담은 장소들을 기반으로 추천 태그를 받는다(`GET /api/v1/recommended-tags`).
     * [placeIds] 가 비면 서버가 인기 태그로 대체한다.
     */
    suspend fun getRecommendedTags(
        placeIds: List<Long>,
        limit: Int,
    ): List<String>

    /**
     * 코스 작성자 팔로우/언팔로우. 반영된 팔로우 상태를 돌려준다.
     * 마이페이지와 같은 엔드포인트지만, 모듈 의존 방향(main → course) 때문에 course 가 직접 호출한다.
     */
    suspend fun setFollowAuthor(
        userId: Long,
        follow: Boolean,
    ): Boolean

    /** 코스 상세(FS-11)를 서버(`GET /service/v1/courses/{id}`)에서 조회한다. */
    suspend fun getCourseDetail(courseId: Long): CourseDetailVO

    /**
     * 초안을 서버에 코스로 생성하고(`POST /api/v1/courses`) 서버가 매긴 courseId 를 돌려준다.
     * 실패 시 예외를 throw 한다.
     */
    suspend fun createCourse(
        draft: CourseDraftVO,
        thumbnailUrl: String?,
        published: Boolean,
    ): Long
}
