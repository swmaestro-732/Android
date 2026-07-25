package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.StateFlow

/**
 * 코스 작성·보관 데이터 접근 규약. 구현은 data 레이어.
 *
 * 세션 목록([savedCourses]·[drafts])과 마지막 완성 코스([lastCompleted])는 앱이 켜져 있는 동안
 * 공유되는 상태이므로 [StateFlow] 로 노출한다. (현재 구현은 인메모리, 콜드 스타트 시 초기화.)
 */
interface CourseRepository {
    /** 저장 완료한 코스 목록(마이 화면 노출). */
    val savedCourses: StateFlow<List<SavedCourseVO>>

    /** 임시저장한 코스 목록(임시저장 화면 노출). */
    val drafts: StateFlow<List<SavedCourseVO>>

    /** 방금 저장 완료한 코스(FS-34-Done 완성 화면 표시용). null 이면 완성 화면이 예시로 폴백. */
    val lastCompleted: StateFlow<CourseCompleteVO?>

    /** 작성 시작 시점의 코스 초안(임시저장 or 빈 초안)을 가져온다. */
    suspend fun getCourseDraft(): CourseDraftVO

    /** 임시저장 코스를 추가한다. */
    fun saveDraft(title: String)

    /** 코스 저장 완료 처리: 완성 화면 데이터를 보관하고 [savedCourses] 에도 추가한다. */
    fun completeCourse(course: CourseCompleteVO?)
}
