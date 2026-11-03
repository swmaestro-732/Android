package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.traces.dto.toVO
import com.chillsam.courmy.course.entity.TraceRouteVO

class TraceRouteDataSource(
    private val apiService: TraceRouteApiService,
) : BaseRemoteDataSource() {
    suspend fun walkingRoute(
        fromPlaceId: Long,
        toPlaceId: Long,
    ): TraceRouteVO = checkResponse(apiService.route(fromPlaceId, toPlaceId)) { it.data.toVO() }
}
