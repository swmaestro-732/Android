package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.map.dto.ExternalMapPlaceSearchEnvelope
import com.chillsam.courmy.main.data.map.dto.MapPlaceSearchEnvelope

class PlaceSearchDataSource(
    private val apiService: PlaceSearchApiService,
) : BaseRemoteDataSource() {
    suspend fun search(
        query: String,
        latitude: Double? = null,
        longitude: Double? = null,
    ): ExternalMapPlaceSearchEnvelope = checkResponse(apiService.search(query, latitude, longitude))

    suspend fun getPlaces(cursor: String?): MapPlaceSearchEnvelope = checkResponse(apiService.getPlaces(cursor))
}
