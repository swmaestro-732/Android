package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import com.chillsam.courmy.main.entity.map.MapBounds

class PlaceMapDataSource(
    private val apiService: PlaceMapApiService,
) : BaseRemoteDataSource() {
    suspend fun getMapPlaces(
        bounds: MapBounds,
        category: String?,
        sort: String?,
        userLat: Double?,
        userLng: Double?,
    ): PlaceMapEnvelope =
        checkResponse(
            apiService.getMapPlaces(
                swLat = bounds.swLat,
                swLng = bounds.swLng,
                neLat = bounds.neLat,
                neLng = bounds.neLng,
                category = category,
                sort = sort,
                userLat = userLat,
                userLng = userLng,
            ),
        )
}
