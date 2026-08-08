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
 * 세션 목록([savedCourses]·[drafts])과 마지막 완성 코스([lastCompleted])는 앱이 켜져 있는 동안
 * 공유되는 상태이므로 [StateFlow] 로 노출한다.
 *
 * 임시저장 초안([drafts])만 기기에 남아 콜드 스타트 후에도 유지된다. 나머지는 인메모리다.
 */
interface CourseRepository {
    /** 저장 완료한 코스 목록(마이 화면 노출). */
    val savedCourses: StateFlow<List<SavedCourseVO>>

    /** 임시저장한 코스 목록(임시저장 화면 노출). 각 항목은 [DraftSummaryVO.id] 로 식별한다. */
    val drafts: StateFlow<List<DraftSummaryVO>>

    /** 방금 저장 완료한 코스(FS-34-Done 완성 화면 표시용). null 이면 완성 화면이 예시로 폴백. */
    val lastCompleted: StateFlow<CourseCompleteVO?>

    /**
     * 작성 시작 시점의 코스 초안을 가져온다.
     * [beginEditDraft] 로 이어서 편집할 초안이 지정돼 있으면 그 전체 내용을, 아니면 빈 초안(추천 태그만)을 반환한다.
     */
    suspend fun getCourseDraft(): CourseDraftVO

    /**
     * 기기에 저장해 둔 초안을 [drafts] 로 올린다. 목록 화면이 열릴 때 한 번 부르면 된다.
     * (작성 화면은 [getCourseDraft] 가 알아서 처리한다.)
     */
    suspend fun loadDrafts()

    /** 다음 [getCourseDraft] 가 돌려줄 "이어서 편집할" 임시저장 초안을 id 로 지정한다. */
    fun beginEditDraft(draftId: String)

    /**
     * 현재 작성 중인 초안을 임시저장한다.
     * 편집 세션 id 로 upsert 하므로, 제목을 바꿔 저장해도 같은 초안이 갱신될 뿐 중복이 생기지 않는다.
     */
    fun saveDraft(draft: CourseDraftVO)

    /** 임시저장 초안 1건을 지운다. 목록([drafts])에서도 즉시 빠진다. */
    fun deleteDraft(draftId: String)

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
