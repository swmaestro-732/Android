package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import javax.inject.Inject

interface MapPlaceSearchRepository {
    suspend fun search(
        query: String,
        cursor: String?,
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
        ): CursorPageVO<MapPlaceVO> = repository.search(query.trim(), cursor)
    }
