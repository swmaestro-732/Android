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
    /**
     * 따라가기 시작. 서버가 traceId 를 발급한다(같은 코스의 진행 중 기록이 있으면 그걸 돌려준다).
     * 다른 코스의 진행 중 기록이 있으면 409(code 4100)로 거절하며, 기존 traceId 는 메시지
     * 문자열(`traceId=N`)로만 준다 — 구조화된 필드가 없다.
     */
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
