package com.chillsam.courmy.course.data.courseCreate

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseEnvelope
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseRequest
import com.chillsam.courmy.course.data.courseCreate.dto.ForkCourseRequest

class CourseCreateDataSource(
    private val apiService: CourseCreateApiService,
) : BaseRemoteDataSource() {
    suspend fun createCourse(request: CreateCourseRequest): CreateCourseEnvelope =
        checkResponse(apiService.createCourse(request))

    /** 포크 저장. 쓰기라 폴백 없이 실패를 그대로 올린다(가짜 성공 금지). */
    suspend fun forkCourse(
        originCourseId: Long,
        request: ForkCourseRequest,
    ): CreateCourseEnvelope = checkResponse(apiService.forkCourse(originCourseId, request))
}
