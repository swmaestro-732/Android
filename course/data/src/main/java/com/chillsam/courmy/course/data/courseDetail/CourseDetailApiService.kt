package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseDetailApiService {
    /** 코스 상세 화면 조합(BFF). 실제로 저장된 코스를 조회한다. */
    @GET("service/v1/courses/{courseId}")
    suspend fun getCourseDetail(
        @Path("courseId") courseId: Long,
    ): Response<CourseDetailEnvelope>
}
