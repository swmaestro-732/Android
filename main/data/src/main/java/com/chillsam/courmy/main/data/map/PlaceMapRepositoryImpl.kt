package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.toVO
import com.chillsam.courmy.main.domain.map.MapPlaceRepository
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
                swLat = swLat,
                swLng = swLng,
                neLat = neLat,
                neLng = neLng,
                category = category.toServerCategory(),
                // 서버는 RELEVANCE·DISTANCE 만 안다. 저장순·평점순은 presentation 의 클라이언트 정렬로 처리한다.
                sort = if (sortOrder == MapSortOrder.DISTANCE) "DISTANCE" else null,
                userLat = userLat,
                userLng = userLng,
            )
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "지도 장소 응답에 data 가 없습니다."
            }
        val vo = data.toVO()
        // 디저트는 서버 PlaceCategory 에 없어 전체를 받아 라벨로 거른다(클라이언트 필터).
        return if (category == MapCategory.DESSERT) vo.filterByLabel(DESSERT_LABEL) else vo
    }

    /**
     * 라벨 기준 클라이언트 필터. 전체 개수는 걸러낸 개수로 바꾸고, 클러스터는 뺀다
     * — 클러스터는 서버가 전체 기준으로 집계한 값이라 필터 결과와 맞지 않는다.
     */
    private fun MapPlacesVO.filterByLabel(label: String): MapPlacesVO {
        val filtered = places.filter { it.category == label }
        return copy(totalCount = filtered.size, places = filtered, clusters = emptyList())
    }

    /**
     * 지도 칩(FS-10) → 서버 `PlaceCategory` enum 이름.
     * 전시·갤러리 ↔ CULTURE, 편집숍 ↔ SHOPPING 으로 맞췄고, 디저트는 대응이 없어 null(전체 조회)이다.
     */
    private fun MapCategory.toServerCategory(): String? =
        when (this) {
            MapCategory.ALL -> null
            MapCategory.CAFE -> "CAFE"
            MapCategory.RESTAURANT -> "RESTAURANT"
            MapCategory.BAR -> "BAR"
            MapCategory.GALLERY -> "CULTURE"
            MapCategory.SELECT_SHOP -> "SHOPPING"
            MapCategory.DESSERT -> null
        }

    private companion object {
        /** [com.chillsam.courmy.main.data.map.dto.toPlaceCategoryLabel] 이 만드는 라벨과 같은 문자열. */
        const val DESSERT_LABEL = "디저트"
    }
}
