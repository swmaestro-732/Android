package com.chillsam.courmy.course.data.placeReviews

import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewDeleteEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 장소 후기 작성·삭제. 조회(`service/v1`, BFF)와 달리 **`api/v1` 도메인 API** 라서 서비스를 나눠 둔다
 * ([com.chillsam.courmy.course.data.courseReviews.CourseReviewWriteApiService] 와 같은 구조).
 *
 * 요청·응답이 코스 후기와 같은 모양이라 DTO 를 재사용한다(목록 조회가 [CourseReviewListApiService]
 * 의 응답 DTO 를 재사용하는 것과 같은 이유). 작성자는 JWT 로 식별한다.
 */
interface PlaceReviewWriteApiService {
    /** 작성 성공은 201 이며 `data.reviewId` 를 준다. 이미 쓴 장소면 409 로 거절된다. */
    @POST("api/v1/places/{placeId}/reviews")
    suspend fun createPlaceReview(
        @Path("placeId") placeId: Long,
        @Body request: CreateCourseReviewRequest,
    ): Response<CreateCourseReviewEnvelope>

    /**
     * 내 후기 삭제(soft delete). 서버는 (userId, placeId, reviewId) 가 모두 맞을 때만 지우고,
     * 남의 리뷰·이미 지운 리뷰는 사유를 숨겨 404 로 답한다.
     */
    @DELETE("api/v1/places/{placeId}/reviews/{reviewId}")
    suspend fun deletePlaceReview(
        @Path("placeId") placeId: Long,
        @Path("reviewId") reviewId: Long,
    ): Response<CourseReviewDeleteEnvelope>
}
