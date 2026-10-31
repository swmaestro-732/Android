package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.home.dto.CourseFeedEnvelope
import com.chillsam.courmy.main.data.home.dto.HomeEnvelope

class HomeFeedDataSource(
    private val apiService: HomeFeedApiService,
) : BaseRemoteDataSource() {
    suspend fun getCourseFeed(
        size: Int,
        cursor: String?,
        category: String?,
    ): CourseFeedEnvelope = checkResponse(apiService.getCourseFeed(size, cursor, category))

    suspend fun getHome(
        userLat: Double?,
        userLng: Double?,
    ): HomeEnvelope = checkResponse(apiService.getHome(userLat, userLng))
}
