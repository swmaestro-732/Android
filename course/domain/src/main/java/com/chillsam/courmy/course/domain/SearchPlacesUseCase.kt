package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CoursePlaceVO
import javax.inject.Inject

/**
 * 장소 검색 UseCase. 검색어가 비면 서버를 부르지 않고 빈 목록을 돌려준다
 * (오버레이는 입력 전 아무것도 보여주지 않는 게 기본 동작이다).
 */
class SearchPlacesUseCase
    @Inject
    constructor(
        private val repository: PlaceRepository,
    ) {
        suspend operator fun invoke(query: String): List<CoursePlaceVO> {
            val keyword = query.trim()
            return if (keyword.isBlank()) emptyList() else repository.searchPlaces(keyword)
        }
    }
