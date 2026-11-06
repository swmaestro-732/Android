package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapPlaceVO
import javax.inject.Inject

/** 서버 관련도 순서를 유지하며 지도 이동이 가능한 등록 장소를 제안한다. */
class SuggestMapPlacesUseCase
    @Inject
    constructor(
        private val repository: MapPlaceSearchRepository,
    ) {
        suspend operator fun invoke(query: String): List<MapPlaceVO> {
            val keyword = query.trim()
            if (keyword.isBlank()) return emptyList()
            return repository.suggestions(keyword).filter {
                val latitude = it.latitude
                val longitude = it.longitude
                it.id > 0 && latitude != null && longitude != null &&
                    latitude in -90.0..90.0 && longitude in -180.0..180.0
            }
        }
    }
