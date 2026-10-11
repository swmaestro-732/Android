package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope

class PlaceMapDataSource(
    private val apiService: PlaceMapApiService,
) : BaseRemoteDataSource() {
    suspend fun getMapPlaces(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        category: String?,
        sort: String?,
        userLat: Double?,
        userLng: Double?,
    ): PlaceMapEnvelope =
        checkResponse(
            apiService.getMapPlaces(
                swLat = swLat,
                swLng = swLng,
                neLat = neLat,
                neLng = neLng,
                category = category,
                sort = sort,
                userLat = userLat,
                userLng = userLng,
            ),
        )
}
