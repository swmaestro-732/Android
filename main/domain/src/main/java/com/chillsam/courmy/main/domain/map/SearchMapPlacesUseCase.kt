package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import javax.inject.Inject

interface MapPlaceSearchRepository {
    suspend fun suggestions(query: String): List<MapPlaceVO>

    suspend fun search(
        query: String,
        bounds: MapBounds,
        latitude: Double?,
        longitude: Double?,
    ): MapPlacesVO
}

/** 검색어 유무와 관계없이 현재 지도 영역의 장소와 클러스터를 조회한다. */
class SearchMapPlacesUseCase
    @Inject
    constructor(
        private val repository: MapPlaceSearchRepository,
    ) {
        suspend operator fun invoke(
            query: String,
            bounds: MapBounds,
            latitude: Double? = null,
            longitude: Double? = null,
        ): MapPlacesVO {
            val validLocation =
                latitude != null && longitude != null &&
                    latitude in -90.0..90.0 && longitude in -180.0..180.0
            return repository.search(
                query.trim(),
                bounds,
                latitude.takeIf { validLocation },
                longitude.takeIf { validLocation },
            )
        }
    }
