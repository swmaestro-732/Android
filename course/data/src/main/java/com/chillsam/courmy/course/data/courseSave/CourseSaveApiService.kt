package com.chillsam.courmy.course.data.courseSave

import com.chillsam.courmy.course.data.courseSave.dto.CourseSaveEnvelope
import com.chillsam.courmy.course.data.courseSave.dto.SaveCourseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseSaveApiService {
    /**
     * 코스 저장. 대상 코스는 경로가 아니라 바디로 보낸다(폴더 지정을 함께 싣기 위해).
     *
     * 경로는 구 `api/v1/courses/save` 에서 `api/v1/saved-courses` 로 옮겼다 — 서버가 구 경로를
     * 삭제할 예정이다. 요청 바디(`SaveCourseRequest`)와 응답 엔벨로프는 구 경로와 같아 DTO 는 그대로 쓴다.
     */
    @POST("api/v1/saved-courses")
    suspend fun save(
        @Body request: SaveCourseRequest,
    ): Response<CourseSaveEnvelope>

    /** 저장 취소. 저장과 같은 이유로 신경로(`api/v1/saved-courses/{courseId}`)를 쓴다. */
    @DELETE("api/v1/saved-courses/{courseId}")
    suspend fun unsave(
        @Path("courseId") courseId: Long,
    ): Response<CourseSaveEnvelope>
}
