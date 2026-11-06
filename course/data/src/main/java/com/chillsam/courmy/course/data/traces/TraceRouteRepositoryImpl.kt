package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.course.domain.TraceRouteRepository
import com.chillsam.courmy.course.entity.TraceRouteVO

class TraceRouteRepositoryImpl(
    private val dataSource: TraceRouteDataSource,
) : TraceRouteRepository {
    override suspend fun walkingRoute(
        fromPlaceId: Long,
        toPlaceId: Long,
    ): TraceRouteVO = dataSource.walkingRoute(fromPlaceId, toPlaceId)
}
