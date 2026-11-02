package com.chillsam.courmy.main.domain.area

import com.chillsam.courmy.main.entity.area.AreaMapVO

interface AreaMapRepository {
    suspend fun getRegions(): List<AreaMapVO>
}
