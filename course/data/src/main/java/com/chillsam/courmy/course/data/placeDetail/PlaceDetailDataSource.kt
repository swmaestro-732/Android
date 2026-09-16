package com.chillsam.courmy.course.data.placeDetail

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.placeDetail.dto.PlaceDetailEnvelope

class PlaceDetailDataSource(
    private val apiService: PlaceDetailApiService,
) : BaseRemoteDataSource() {
    suspend fun getPlaceDetail(placeId: Long): PlaceDetailEnvelope = checkResponse(apiService.getPlaceDetail(placeId))
}
