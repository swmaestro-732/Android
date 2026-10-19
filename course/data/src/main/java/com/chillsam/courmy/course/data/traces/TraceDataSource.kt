package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.traces.dto.CheckInPlaceRequest
import com.chillsam.courmy.course.data.traces.dto.EndTraceRequest
import com.chillsam.courmy.course.data.traces.dto.TraceActionEnvelope
import com.chillsam.courmy.course.data.traces.dto.TraceEnvelope

class TraceDataSource(
    private val apiService: TraceApiService,
) : BaseRemoteDataSource() {
    suspend fun startTrace(courseId: Long): TraceEnvelope = checkResponse(apiService.startTrace(courseId))

    suspend fun checkInPlace(
        traceId: Long,
        request: CheckInPlaceRequest,
    ): TraceActionEnvelope = checkResponse(apiService.checkInPlace(traceId, request))

    suspend fun endTrace(
        traceId: Long,
        request: EndTraceRequest,
    ): TraceActionEnvelope = checkResponse(apiService.endTrace(traceId, request))
}
