package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseEditVO
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * 내 코스 관리(편집·삭제) 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 작성자 본인만 가능한 동작이며 서버가 JWT 로 판정한다.
 */
interface CourseManageRepository {
    /** 편집 화면이 공개 설정을 채우려고 읽는다. 알 수 없으면 null. */
    suspend fun getVisibility(courseId: Long): CourseVisibility?

    suspend fun updateCourse(
        courseId: Long,
        edit: CourseEditVO,
    )

    suspend fun deleteCourse(courseId: Long)
}
