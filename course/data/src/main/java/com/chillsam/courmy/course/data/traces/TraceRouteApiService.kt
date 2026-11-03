package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.course.data.traces.dto.TraceRouteEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 두 장소 사이 도보 경로 조회. 따라가기 지도에 실제 보행 경로를 그릴 때 쓴다.
 *
 * 세션(traceId)·코스에 묶지 않는다 — 장소 id 두 개만 보낸다. 도보 불가 구간도 200 으로 오고
 * `walkable=false` 로 구분한다(503 은 Tmap 호출 실패).
 */
interface TraceRouteApiService {
    @GET("api/v1/traces/route")
    suspend fun route(
        @Query("fromPlaceId") fromPlaceId: Long,
        @Query("toPlaceId") toPlaceId: Long,
    ): Response<TraceRouteEnvelope>
}
