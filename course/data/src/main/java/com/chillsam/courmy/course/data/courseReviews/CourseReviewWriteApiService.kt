package com.chillsam.courmy.course.data.courseReviews

import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewDeleteEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 후기 작성·삭제. 조회(`service/v1`, BFF)와 달리 **`api/v1` 도메인 API** 라서 서비스를 나눠 둔다.
 * 둘 다 액세스 토큰이 필요하다(서버 `@AccessTokenRequired`).
 */
interface CourseReviewWriteApiService {
    /** 작성 성공은 201 이며 `data.reviewId` 를 준다. 이미 쓴 코스면 409 로 거절된다. */
    @POST("api/v1/courses/{courseId}/reviews")
    suspend fun createCourseReview(
        @Path("courseId") courseId: Long,
        @Body request: CreateCourseReviewRequest,
    ): Response<CreateCourseReviewEnvelope>

    /**
     * 내 후기 삭제(soft delete). 서버는 (userId, courseId, reviewId) 가 모두 맞을 때만 지우고,
     * 남의 리뷰·이미 지운 리뷰는 사유를 숨겨 404 로 답한다.
     */
    @DELETE("api/v1/courses/{courseId}/reviews/{reviewId}")
    suspend fun deleteCourseReview(
        @Path("courseId") courseId: Long,
        @Path("reviewId") reviewId: Long,
    ): Response<CourseReviewDeleteEnvelope>
}
