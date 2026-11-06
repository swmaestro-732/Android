package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.TraceRouteVO

/**
 * 두 장소 사이 도보 경로 조회 계약(`GET /api/v1/traces/route`). 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 도보 불가 구간은 예외가 아니라 [TraceRouteVO.walkable] = false 로 정상 응답한다 —
 * Tmap 호출 실패(503)만 예외다.
 */
interface TraceRouteRepository {
    suspend fun walkingRoute(
        fromPlaceId: Long,
        toPlaceId: Long,
    ): TraceRouteVO
}
