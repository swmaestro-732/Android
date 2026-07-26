package com.chillsam.courmy.course.data

import com.chillsam.courmy.common.data.ApiResponse
import com.chillsam.courmy.course.data.dto.CourseDetailScreenDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseApiService {
    /** 코스 상세(화면 조합 BFF). 공개 엔드포인트라 인증 없이 조회된다. */
    @GET("service/v1/courses/{courseId}")
    suspend fun getCourseDetail(
        @Path("courseId") courseId: String,
    ): Response<ApiResponse<CourseDetailScreenDto>>
}
