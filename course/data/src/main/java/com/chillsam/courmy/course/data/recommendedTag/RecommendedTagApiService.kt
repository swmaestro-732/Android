package com.chillsam.courmy.course.data.recommendedTag

import com.chillsam.courmy.course.data.recommendedTag.dto.RecommendedTagsEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecommendedTagApiService {
    /**
     * 코스에 담은 장소들을 기반으로 태그를 추천받는다.
     * [placeIds] 가 비어 있으면 서버가 인기 태그로 대체한다.
     */
    @GET("api/v1/recommended-tags")
    suspend fun getRecommendedTags(
        @Query("placeIds") placeIds: List<Long>,
        @Query("limit") limit: Int,
    ): Response<RecommendedTagsEnvelope>
}
