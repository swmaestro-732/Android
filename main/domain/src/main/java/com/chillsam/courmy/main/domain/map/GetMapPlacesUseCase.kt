package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import com.chillsam.courmy.main.entity.map.MapSortOrder
import javax.inject.Inject

/** 지도 영역 장소 탐색 UseCase. 파라미터 의미는 [MapPlaceRepository.getMapPlaces] 를 따른다. */
class GetMapPlacesUseCase
    @Inject
    constructor(
        private val repository: MapPlaceRepository,
    ) {
        suspend operator fun invoke(
            swLat: Double,
            swLng: Double,
            neLat: Double,
            neLng: Double,
            category: MapCategory = MapCategory.ALL,
            sortOrder: MapSortOrder = MapSortOrder.SAVED,
            userLat: Double? = null,
            userLng: Double? = null,
        ): MapPlacesVO =
            repository.getMapPlaces(
                swLat = swLat,
                swLng = swLng,
                neLat = neLat,
                neLng = neLng,
                category = category,
                sortOrder = sortOrder,
                userLat = userLat,
                userLng = userLng,
            )
    }
