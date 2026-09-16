package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CoursePlaceVO
import javax.inject.Inject

/**
 * 외부 지도 장소 검색(`GET /api/v1/places/search`).
 *
 * 등록된 장소만 도는 [SearchPlacesUseCase] 로는 찾을 수 없는 장소를 담을 때 쓴다.
 * 검색어가 비면 서버가 400 을 주므로 호출부에서 걸러 부른다.
 */
class SearchExternalPlacesUseCase
    @Inject
    constructor(
        private val repository: PlaceRepository,
    ) {
        suspend operator fun invoke(query: String): List<CoursePlaceVO> = repository.searchExternalPlaces(query)
    }
