package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.home.dto.CourseFeedEnvelope

class HomeFeedDataSource(
    private val apiService: HomeFeedApiService,
) : BaseRemoteDataSource() {
    suspend fun getCourseFeed(size: Int): CourseFeedEnvelope = checkResponse(apiService.getCourseFeed(size))
}
