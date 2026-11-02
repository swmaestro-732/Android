package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.toPageVO
import com.chillsam.courmy.main.data.map.dto.toVO
import com.chillsam.courmy.main.domain.map.MapPlaceSearchRepository
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapPlacesVO

class MapPlaceSearchRepositoryImpl(
    private val dataSource: PlaceSearchDataSource,
) : MapPlaceSearchRepository {
    override suspend fun suggestions(query: String): List<MapPlaceVO> {
        val response = dataSource.suggestions(query)
        return requireNotNull(response.data) { response.message ?: "장소 제안 응답에 data가 없습니다." }.toPageVO().items
    }

    override suspend fun search(
        query: String,
        bounds: MapBounds,
        latitude: Double?,
        longitude: Double?,
    ): MapPlacesVO {
        val response = dataSource.search(query, bounds, latitude, longitude)
        return requireNotNull(response.data) { response.message ?: "장소 검색 응답에 data가 없습니다." }.toVO()
    }
}
