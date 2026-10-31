package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.toVO
import com.chillsam.courmy.main.domain.map.MapPlaceRepository
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import com.chillsam.courmy.main.entity.map.MapSortOrder

class PlaceMapRepositoryImpl(
    private val dataSource: PlaceMapDataSource,
) : MapPlaceRepository {
    override suspend fun getMapPlaces(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        category: MapCategory,
        sortOrder: MapSortOrder,
        userLat: Double?,
        userLng: Double?,
    ): MapPlacesVO {
        val envelope =
            dataSource.getMapPlaces(
                bounds = MapBounds(swLat, swLng, neLat, neLng),
                category = category.takeUnless { it == MapCategory.ALL }?.name,
                // 서버는 RELEVANCE·DISTANCE 만 안다. 저장순·평점순은 presentation 의 클라이언트 정렬로 처리한다.
                sort = if (sortOrder == MapSortOrder.DISTANCE) "DISTANCE" else null,
                userLat = userLat,
                userLng = userLng,
            )
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "지도 장소 응답에 data 가 없습니다."
            }
        return data.toVO()
    }
}
