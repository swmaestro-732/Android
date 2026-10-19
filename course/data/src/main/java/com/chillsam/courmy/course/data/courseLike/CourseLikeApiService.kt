package com.chillsam.courmy.course.data.courseLike

import com.chillsam.courmy.course.data.courseLike.dto.CourseLikeEnvelope
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseLikeApiService {
    /**
     * 코스 좋아요. 누르는 사람은 JWT 로 식별된다 — 배포된 swagger 는 `userId` 를 필수 쿼리로
     * 표시하지만 서버 실제 시그니처는 `@CurrentUserId` 다(코스 생성과 같은 springdoc 오표기).
     */
    @POST("api/v1/courses/{courseId}/likes")
    suspend fun like(
        @Path("courseId") courseId: Long,
    ): Response<CourseLikeEnvelope>

    @DELETE("api/v1/courses/{courseId}/likes")
    suspend fun unlike(
        @Path("courseId") courseId: Long,
    ): Response<CourseLikeEnvelope>
}
