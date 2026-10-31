package com.chillsam.courmy.course.data.placeReviews

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewDeleteEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewEnvelope
import com.chillsam.courmy.course.data.courseReviews.dto.CreateCourseReviewRequest

class PlaceReviewListDataSource(
    private val apiService: PlaceReviewListApiService,
    private val writeApiService: PlaceReviewWriteApiService,
) : BaseRemoteDataSource() {
    /**
     * 장소 후기 조회.
     *
     * [PlaceReviewListFallback] 은 디버그 빌드에서 **요청이 실패했을 때만** 예시 응답으로 받아 주고,
     * 릴리스에서는 그대로 통과시켜 예외가 화면 에러로 올라간다.
     */
    suspend fun getPlaceReviews(
        placeId: Long,
        sort: String,
        order: String,
        cursor: String?,
        size: Int,
    ): CourseReviewListEnvelope =
        PlaceReviewListFallback.resolve {
            checkResponse(
                apiService.getPlaceReviews(
                    placeId = placeId,
                    sort = sort,
                    order = order,
                    cursor = cursor,
                    size = size,
                ),
            )
        }

    /** 후기 작성. 실패(409 중복 등)는 폴백 없이 예외로 올린다 — 쓰기를 가짜 성공으로 만들지 않는다. */
    suspend fun createPlaceReview(
        placeId: Long,
        request: CreateCourseReviewRequest,
    ): CreateCourseReviewEnvelope = checkResponse(writeApiService.createPlaceReview(placeId, request))

    /** 후기 삭제. */
    suspend fun deletePlaceReview(
        placeId: Long,
        reviewId: Long,
    ): CourseReviewDeleteEnvelope = checkResponse(writeApiService.deletePlaceReview(placeId, reviewId))
}
