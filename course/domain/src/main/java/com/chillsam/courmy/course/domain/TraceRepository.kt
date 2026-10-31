package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.TraceVisitVO

/**
 * 코스 따라가기 기록(trace) Repository.
 *
 * 기록은 통계용 부가 기능이다 — 구현은 실패 시 예외를 throw 하고, 삼켜서 따라가기 UX 를
 * 막지 않을지는 호출부(ViewModel)가 정한다.
 */
interface TraceRepository {
    /** 따라가기 시작(`POST /api/v1/courses/{courseId}/traces`). 서버가 발급한 traceId 를 돌려준다. */
    suspend fun startTrace(courseId: Long): Long

    /** 장소 방문 체크(`POST /api/v1/traces/{traceId}/places`). */
    suspend fun checkInPlace(
        traceId: Long,
        visit: TraceVisitVO,
    )

    /**
     * 따라가기 종료(`POST /api/v1/traces/{traceId}/end`). [visits] 는 이번 따라가기의 전체 방문 기록이다.
     * [distanceMeters] 는 위치 추적 전이라 0 을 보낸다(서버 최소값 0 허용).
     */
    suspend fun endTrace(
        traceId: Long,
        visits: List<TraceVisitVO>,
        durationMinutes: Int,
        distanceMeters: Int,
    )
}
