package com.chillsam.courmy.course.data.directions.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/directions/walking` 요청. 코스에 담은 장소를 **순서대로** 넣는다.
 *
 * 필드명이 `latitude`/`longitude` 가 아니라 짧은 [lat]·[lng] 다(다른 API 들과 다르니 주의).
 */
@Serializable
data class WalkingDirectionsRequest(
    val points: List<WalkingPointDTO>,
)

@Serializable
data class WalkingPointDTO(
    val lat: Double,
    val lng: Double,
)

/**
 * 응답. [WalkingDirectionsDataDTO.segments] 는 **구간별 도보 분(minutes)** 이며
 * 장소가 N 곳이면 N-1 개가 온다(마지막 장소 뒤에는 갈 곳이 없다).
 */
@Serializable
data class WalkingDirectionsEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: WalkingDirectionsDataDTO? = null,
)

@Serializable
data class WalkingDirectionsDataDTO(
    val segments: List<Int> = emptyList(),
)
