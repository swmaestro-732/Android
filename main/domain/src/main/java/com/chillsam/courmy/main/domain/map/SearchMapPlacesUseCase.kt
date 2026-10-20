package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import javax.inject.Inject

interface MapPlaceSearchRepository {
    suspend fun search(
        query: String,
        cursor: String?,
        latitude: Double?,
        longitude: Double?,
    ): CursorPageVO<MapPlaceVO>
}

/** 빈 검색어는 등록 장소 목록을 조회한다(지도 탭의 목록 보기). */
class SearchMapPlacesUseCase
    @Inject
    constructor(
        private val repository: MapPlaceSearchRepository,
    ) {
        suspend operator fun invoke(
            query: String,
            cursor: String? = null,
            latitude: Double? = null,
            longitude: Double? = null,
        ): CursorPageVO<MapPlaceVO> {
            val validLocation =
                latitude != null && longitude != null &&
                    latitude in -90.0..90.0 && longitude in -180.0..180.0
            return repository.search(
                query.trim(),
                cursor,
                latitude.takeIf { validLocation },
                longitude.takeIf { validLocation },
            )
        }
    }
