package com.chillsam.courmy.course.domain

/**
 * 내 코스 관리 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 작성자 본인만 가능한 동작이며 서버가 JWT 로 판정한다.
 */
interface CourseManageRepository {
    suspend fun deleteCourse(courseId: Long)
}
