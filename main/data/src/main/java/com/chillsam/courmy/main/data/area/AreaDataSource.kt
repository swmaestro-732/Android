package com.chillsam.courmy.main.data.area

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.area.dto.AreaSearchEnvelope

class AreaDataSource(
    private val apiService: AreaApiService,
) : BaseRemoteDataSource() {
    suspend fun searchAreas(keyword: String): AreaSearchEnvelope = checkResponse(apiService.searchAreas(keyword))
}
