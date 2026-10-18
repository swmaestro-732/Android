package com.chillsam.courmy.course.data.traces.dto

import kotlinx.serialization.Serializable

/**
 * 따라가기 기록 API DTO(`/api/v1/courses/{courseId}/traces`, `/api/v1/traces/{traceId}/…`).
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 시작 응답의 `startedAt`·`visitedPlaces`(이어가기용 기존 기록)는 화면이 쓰지 않아 매핑하지 않는다
 * — 이어가기 UX 가 생기면 함께 싣는다.
 */
@Serializable
data class TraceEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseTraceDTO? = null,
)

@Serializable
data class CourseTraceDTO(
    val traceId: Long? = null,
    val courseId: Long? = null,
)

/** data 없는 공통 응답(방문 체크·종료). */
@Serializable
data class TraceActionEnvelope(
    val code: Int? = null,
    val message: String? = null,
)

/** 방문 체크 요청. [visitedAt] 은 ISO-8601(UTC). */
@Serializable
data class CheckInPlaceRequest(
    val placeId: Long,
    val visitedAt: String,
)

@Serializable
data class EndTraceRequest(
    val visitedPlaces: List<VisitedPlaceRequest>,
    val durationMinutes: Int,
    val distanceMeters: Int,
)

@Serializable
data class VisitedPlaceRequest(
    val placeId: Long,
    val visitedAt: String,
)
