package com.chillsam.courmy.course.data.place

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.place.dto.PlaceSearchEnvelope

class PlaceDataSource(
    private val apiService: PlaceApiService,
) : BaseRemoteDataSource() {
    suspend fun searchPlaces(query: String): PlaceSearchEnvelope = checkResponse(apiService.searchPlaces(query))
}
