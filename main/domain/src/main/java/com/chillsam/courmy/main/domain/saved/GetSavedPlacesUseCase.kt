package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO
import javax.inject.Inject

/** 저장한 장소 목록 조회. 서버가 size 를 1~50 으로 제한하므로 범위를 맞춰 넘긴다(코스 탭과 동일). */
class GetSavedPlacesUseCase
    @Inject
    constructor(
        private val repository: SavedPlaceRepository,
    ) {
        suspend operator fun invoke(
            size: Int = DEFAULT_SIZE,
            cursor: String? = null,
            visited: Boolean? = null,
        ): CursorPageVO<SavedPlaceVO> = repository.getSavedPlaces(size.coerceIn(MIN_SIZE, MAX_SIZE), cursor, visited)

        companion object {
            const val DEFAULT_SIZE = 20
            private const val MIN_SIZE = 1
            private const val MAX_SIZE = 50
        }
    }
