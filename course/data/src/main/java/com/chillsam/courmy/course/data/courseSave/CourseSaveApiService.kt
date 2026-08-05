package com.chillsam.courmy.course.data.courseSave

import com.chillsam.courmy.course.data.courseSave.dto.CourseSaveEnvelope
import com.chillsam.courmy.course.data.courseSave.dto.SaveCourseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseSaveApiService {
    /** 코스 저장. 대상 코스는 경로가 아니라 바디로 보낸다(폴더 지정을 함께 싣기 위해). */
    @POST("api/v1/courses/save")
    suspend fun save(
        @Body request: SaveCourseRequest,
    ): Response<CourseSaveEnvelope>

    @DELETE("api/v1/courses/save/{courseId}")
    suspend fun unsave(
        @Path("courseId") courseId: Long,
    ): Response<CourseSaveEnvelope>
}
