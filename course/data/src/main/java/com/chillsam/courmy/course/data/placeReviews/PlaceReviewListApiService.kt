package com.chillsam.courmy.course.data.placeReviews

import com.chillsam.courmy.course.data.courseReviews.dto.CourseReviewListEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceReviewListApiService {
    /**
     * 장소 후기 목록(BFF). 코스 후기와 같은 이유(작성자 프로필 병합)로 Service 가 담당하며
     * 응답 구조도 같아 DTO 를 재사용한다([CourseReviewListEnvelope]).
     *
     * 조회자(`viewerId`)는 JWT 에서 읽으므로 쿼리로 싣지 않는다.
     * [size] 는 서버가 1~50 만 받는다(벗어나면 400).
     *
     * [cursor] 는 직전 응답의 `nextCursor` 를 그대로 돌려주는 불투명 문자열이다. 첫 페이지는 null 로 둔다
     * (Retrofit 은 null [Query] 를 아예 빼고 보낸다).
     */
    @GET("service/v1/places/{placeId}/reviews")
    suspend fun getPlaceReviews(
        @Path("placeId") placeId: Long,
        @Query("sort") sort: String,
        @Query("order") order: String,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): Response<CourseReviewListEnvelope>
}
