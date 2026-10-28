package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.course.data.traces.dto.CheckInPlaceRequest
import com.chillsam.courmy.course.data.traces.dto.EndTraceRequest
import com.chillsam.courmy.course.data.traces.dto.VisitedPlaceRequest
import com.chillsam.courmy.course.domain.TraceRepository
import com.chillsam.courmy.course.entity.TraceVisitVO

class TraceRepositoryImpl(
    private val dataSource: TraceDataSource,
) : TraceRepository {
    override suspend fun startTrace(courseId: Long): Long {
        val envelope = dataSource.startTrace(courseId)
        return requireNotNull(envelope.data?.traceId) { "따라가기 시작 응답에 traceId 가 없습니다." }
    }

    override suspend fun checkInPlace(
        traceId: Long,
        visit: TraceVisitVO,
    ) {
        dataSource.checkInPlace(
            traceId = traceId,
            request = CheckInPlaceRequest(placeId = visit.placeId, visitedAt = visit.visitedAt),
        )
    }

    override suspend fun endTrace(
        traceId: Long,
        visits: List<TraceVisitVO>,
        durationMinutes: Int,
        distanceMeters: Int,
    ) {
        dataSource.endTrace(
            traceId = traceId,
            request =
                EndTraceRequest(
                    visitedPlaces = visits.map { VisitedPlaceRequest(placeId = it.placeId, visitedAt = it.visitedAt) },
                    durationMinutes = durationMinutes,
                    distanceMeters = distanceMeters,
                ),
        )
    }
}
