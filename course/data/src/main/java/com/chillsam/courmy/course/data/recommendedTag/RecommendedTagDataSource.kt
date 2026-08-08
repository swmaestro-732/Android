package com.chillsam.courmy.course.data.recommendedTag

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.recommendedTag.dto.RecommendedTagsEnvelope

class RecommendedTagDataSource(
    private val apiService: RecommendedTagApiService,
) : BaseRemoteDataSource() {
    suspend fun getRecommendedTags(
        placeIds: List<Long>,
        limit: Int,
    ): RecommendedTagsEnvelope = checkResponse(apiService.getRecommendedTags(placeIds, limit))
}
