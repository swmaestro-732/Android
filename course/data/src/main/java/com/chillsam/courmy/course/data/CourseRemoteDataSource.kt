package com.chillsam.courmy.course.data

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.dto.CourseDetailScreenDto

class CourseRemoteDataSource(
    private val apiService: CourseApiService,
) : BaseRemoteDataSource() {
    suspend fun getCourseDetail(courseId: String): CourseDetailScreenDto =
        checkResponse(apiService.getCourseDetail(courseId)).data
            ?: CourseDetailScreenDto()
}
