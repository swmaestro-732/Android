package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.course.data.traces.dto.CheckInPlaceRequest
import com.chillsam.courmy.course.data.traces.dto.EndTraceRequest
import com.chillsam.courmy.course.data.traces.dto.TraceActionEnvelope
import com.chillsam.courmy.course.data.traces.dto.TraceEnvelope
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface TraceApiService {
    /** 따라가기 시작. 서버가 traceId 를 발급한다(진행 중 기록이 있으면 그걸 돌려준다). */
    @POST("api/v1/courses/{courseId}/traces")
    suspend fun startTrace(
        @Path("courseId") courseId: Long,
    ): Response<TraceEnvelope>

    @POST("api/v1/traces/{traceId}/places")
    suspend fun checkInPlace(
        @Path("traceId") traceId: Long,
        @Body request: CheckInPlaceRequest,
    ): Response<TraceActionEnvelope>

    @POST("api/v1/traces/{traceId}/end")
    suspend fun endTrace(
        @Path("traceId") traceId: Long,
        @Body request: EndTraceRequest,
    ): Response<TraceActionEnvelope>
}
