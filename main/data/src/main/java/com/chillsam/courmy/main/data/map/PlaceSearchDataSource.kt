package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.map.dto.MapPlaceSearchEnvelope
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import com.chillsam.courmy.main.entity.map.MapBounds

class PlaceSearchDataSource(
    private val apiService: PlaceSearchApiService,
) : BaseRemoteDataSource() {
    suspend fun suggestions(query: String): MapPlaceSearchEnvelope = checkResponse(apiService.suggestions(query))

    suspend fun search(
        query: String,
        bounds: MapBounds,
        category: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
    ): PlaceMapEnvelope =
        checkResponse(
            apiService.search(
                query = query,
                swLat = bounds.swLat,
                swLng = bounds.swLng,
                neLat = bounds.neLat,
                neLng = bounds.neLng,
                category = category,
                latitude = latitude,
                longitude = longitude,
            ),
        )
}
