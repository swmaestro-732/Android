package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDraftVO

/**
 * 코스 작성 데이터 접근 규약. 구현은 data 레이어.
 */
interface CourseRepository {
    /** 작성 시작 시점의 코스 초안(임시저장 or 빈 초안)을 가져온다. */
    suspend fun getCourseDraft(): CourseDraftVO
}
