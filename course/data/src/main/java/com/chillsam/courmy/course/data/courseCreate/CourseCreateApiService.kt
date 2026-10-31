package com.chillsam.courmy.course.data.courseCreate

import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseEnvelope
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseRequest
import com.chillsam.courmy.course.data.courseCreate.dto.ForkCourseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseCreateApiService {
    /** 코스 생성. 성공 시 201 과 함께 courseId 를 돌려준다. 작성자는 JWT 로 식별된다. */
    @POST("api/v1/courses")
    suspend fun createCourse(
        @Body request: CreateCourseRequest,
    ): Response<CreateCourseEnvelope>

    /**
     * 코스 포크 — 포크 자체가 **새 코스를 만든다**(응답이 생성과 같은 courseId 엔벨로프,
     * swagger `ApiResponseCourseIdResponse` 라 [CreateCourseEnvelope] 를 재사용한다).
     * 그래서 포크로 시작한 작성은 [createCourse] 대신 이쪽을 부른다.
     *
     * 서버는 이 경로에서만 원본 장소 유지 규칙
     * ([com.chillsam.courmy.course.entity.CourseForkRule])을 검증하고, 어기면
     * `400 FORK_PLACES_NOT_KEPT(4004)`, 원본이 없으면 404 를 돌려준다.
     */
    @POST("api/v1/courses/{courseId}/forks")
    suspend fun forkCourse(
        @Path("courseId") originCourseId: Long,
        @Body request: ForkCourseRequest,
    ): Response<CreateCourseEnvelope>
}
