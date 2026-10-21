package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope

class CourseDetailDataSource(
    private val apiService: CourseDetailApiService,
) : BaseRemoteDataSource() {
    suspend fun getCourseDetail(courseId: Long): CourseDetailEnvelope =
        checkResponse(apiService.getCourseDetail(courseId))
            // 릴리스 변형에서는 no-op 이다 — [CourseDetailReviewFallback] 참고.
            .let(CourseDetailReviewFallback::applyTo)
}
