package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope

class CourseDetailDataSource(
    private val apiService: CourseDetailApiService,
) : BaseRemoteDataSource() {
    suspend fun getCourseDetail(courseId: Long): CourseDetailEnvelope =
        checkResponse(apiService.getCourseDetail(courseId))
}
