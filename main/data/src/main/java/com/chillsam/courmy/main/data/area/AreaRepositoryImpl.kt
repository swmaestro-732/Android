package com.chillsam.courmy.main.data.area

import com.chillsam.courmy.main.data.area.dto.toVOList
import com.chillsam.courmy.main.domain.area.AreaRepository
import com.chillsam.courmy.main.entity.area.AreaVO

class AreaRepositoryImpl(
    private val dataSource: AreaDataSource,
) : AreaRepository {
    override suspend fun searchAreas(keyword: String): List<AreaVO> =
        dataSource
            .searchAreas(keyword)
            .data
            .orEmpty()
            .toVOList()
}
