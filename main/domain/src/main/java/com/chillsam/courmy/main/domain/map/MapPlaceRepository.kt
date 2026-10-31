package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import com.chillsam.courmy.main.entity.map.MapSortOrder

/**
 * 지도 영역 장소 탐색(`GET /api/v1/places/map`) 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 영역은 남서([swLat]/[swLng])·북동([neLat]/[neLng]) 꼭짓점으로 넘긴다 — 서버 계약이 bounds 기반이라
 * 중심+반경이 아니라 사각형을 받는다.
 */
interface MapPlaceRepository {
    /**
     * [category] 는 그대로 넘기면 된다 — 서버 enum 과의 대응(지원하지 않는 값의 클라이언트 필터링 포함)은
     * 구현이 책임진다. [userLat]/[userLng] 는 [MapSortOrder.DISTANCE] 일 때 거리 계산 기준점이다.
     */
    suspend fun getMapPlaces(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        category: MapCategory = MapCategory.ALL,
        sortOrder: MapSortOrder = MapSortOrder.SAVED,
        userLat: Double? = null,
        userLng: Double? = null,
    ): MapPlacesVO
}
