package com.chillsam.courmy.course.data.courseCreate

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseEnvelope
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseRequest

class CourseCreateDataSource(
    private val apiService: CourseCreateApiService,
) : BaseRemoteDataSource() {
    suspend fun createCourse(request: CreateCourseRequest): CreateCourseEnvelope =
        checkResponse(apiService.createCourse(request))
}
