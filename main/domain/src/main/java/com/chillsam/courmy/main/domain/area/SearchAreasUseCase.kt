package com.chillsam.courmy.main.domain.area

import com.chillsam.courmy.main.entity.area.AreaVO
import javax.inject.Inject

class SearchAreasUseCase
    @Inject
    constructor(
        private val repository: AreaRepository,
    ) {
        /** 공백만 있는 검색어는 서버를 부르지 않고 빈 목록으로 끝낸다. */
        suspend operator fun invoke(keyword: String): List<AreaVO> {
            val trimmed = keyword.trim()
            return if (trimmed.isEmpty()) emptyList() else repository.searchAreas(trimmed)
        }
    }
