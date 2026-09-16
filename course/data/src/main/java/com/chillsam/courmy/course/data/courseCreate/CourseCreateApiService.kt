package com.chillsam.courmy.course.data.courseCreate

import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseEnvelope
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CourseCreateApiService {
    /** 코스 생성. 성공 시 201 과 함께 courseId 를 돌려준다. 작성자는 JWT 로 식별된다. */
    @POST("api/v1/courses")
    suspend fun createCourse(
        @Body request: CreateCourseRequest,
    ): Response<CreateCourseEnvelope>
}
