package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.course.data.traces.dto.CheckInPlaceRequest
import com.chillsam.courmy.course.data.traces.dto.EndTraceRequest
import com.chillsam.courmy.course.data.traces.dto.VisitedPlaceRequest
import com.chillsam.courmy.course.domain.ActiveTraceConflictException
import com.chillsam.courmy.course.domain.TraceRepository
import com.chillsam.courmy.course.entity.TraceVisitVO
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TraceRepositoryImpl(
    private val dataSource: TraceDataSource,
) : TraceRepository {
    override suspend fun startTrace(courseId: Long): Long {
        val envelope =
            try {
                dataSource.startTrace(courseId)
            } catch (e: HttpResponseException) {
                throw e.toActiveTraceConflictOrSelf()
            }
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

/**
 * 진행 중 세션 충돌(HTTP 409, code 4100)이면 [ActiveTraceConflictException] 으로 바꾼다.
 *
 * 서버는 기존 세션 id 를 구조화된 필드가 아니라 메시지 문자열로만 준다
 * (`"이미 따라가는 중인 코스가 있습니다: traceId=2, courseId=169"`) — `traceId=N` 을 추출한다.
 * 충돌이 아니거나 본문을 해석하지 못하면 원래 예외를 그대로 돌려준다.
 */
private fun HttpResponseException.toActiveTraceConflictOrSelf(): Exception {
    if (rawCode != HTTP_CONFLICT) return this
    val body =
        runCatching { Json.parseToJsonElement(cause?.message.orEmpty()).jsonObject }.getOrNull()
            ?: return this
    val serverCode = runCatching { body["code"]?.jsonPrimitive?.intOrNull }.getOrNull()
    if (serverCode != ACTIVE_TRACE_CONFLICT_CODE) return this
    val serverMessage = runCatching { body["message"]?.jsonPrimitive?.contentOrNull }.getOrNull()
    val activeTraceId =
        serverMessage
            ?.let { TRACE_ID_PATTERN.find(it) }
            ?.groupValues
            ?.getOrNull(1)
            ?.toLongOrNull()
    return ActiveTraceConflictException(
        activeTraceId = activeTraceId,
        message = serverMessage ?: message,
        cause = this,
    )
}

private const val HTTP_CONFLICT = 409

/** 서버 비즈니스 코드: 이미 진행 중인 따라가기 세션이 있음. */
private const val ACTIVE_TRACE_CONFLICT_CODE = 4100

private val TRACE_ID_PATTERN = Regex("""traceId=(\d+)""")
