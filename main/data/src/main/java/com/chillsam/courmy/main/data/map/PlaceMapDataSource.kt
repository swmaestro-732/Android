package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope

class PlaceMapDataSource(
    private val apiService: PlaceMapApiService,
) : BaseRemoteDataSource() {
    /**
     * 지도 영역 장소 탐색.
     *
     * [PlaceMapFallback] 은 디버그 빌드에서 **요청이 실패했을 때만** 예시 응답으로 받아 주고,
     * 릴리스에서는 그대로 통과시켜 예외가 화면 에러로 올라간다. 빈 결과는 정상 응답이라 덮지 않는다.
     */
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
        PlaceMapFallback.resolve {
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
}
