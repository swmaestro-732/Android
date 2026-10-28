package com.chillsam.courmy.main.domain.area

import com.chillsam.courmy.main.entity.area.AreaVO

interface AreaRepository {
    /** 검색어에 걸리는 행정구역 목록. 결과가 없으면 빈 목록. */
    suspend fun searchAreas(keyword: String): List<AreaVO>
}
