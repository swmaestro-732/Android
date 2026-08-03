package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseDetailApiService {
    /** 코스 상세 화면 조합(BFF). 목 데이터상 courseId=1 만 200, 그 외 404. */
    @GET("service/v1/courses/{courseId}")
    suspend fun getCourseDetail(
        @Path("courseId") courseId: Long,
        @Query("mock") mock: Boolean = true,
    ): Response<CourseDetailEnvelope>
}
