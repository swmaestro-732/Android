package com.chillsam.courmy.course.entity

/**
 * 따라가기 중 방문 체크 1건. [visitedAt] 은 ISO-8601(UTC) 문자열이다 — entity 를 순수 Kotlin/JVM 으로
 * 유지하려고 시간 타입 대신 직렬화 가능한 문자열을 쓴다(생성은 presentation 몫).
 */
data class TraceVisitVO(
    val placeId: Long,
    val visitedAt: String,
)
