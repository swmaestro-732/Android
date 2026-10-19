package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.data.map.dto.toPageVO
import com.chillsam.courmy.main.domain.map.MapPlaceSearchRepository
import com.chillsam.courmy.main.entity.map.MapPlaceVO

class MapPlaceSearchRepositoryImpl(
    private val dataSource: PlaceSearchDataSource,
) : MapPlaceSearchRepository {
    override suspend fun search(
        query: String,
        cursor: String?,
    ): CursorPageVO<MapPlaceVO> {
        if (query.isBlank()) {
            val response = dataSource.getPlaces(cursor)
            return requireNotNull(response.data) { response.message ?: "장소 목록 응답에 data가 없습니다." }.toPageVO()
        }
        val response = dataSource.search(query)
        return requireNotNull(response.data) { response.message ?: "장소 검색 응답에 data가 없습니다." }.toPageVO()
    }
}
