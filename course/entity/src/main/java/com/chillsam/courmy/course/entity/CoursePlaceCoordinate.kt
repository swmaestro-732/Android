package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/** 도보 시간을 구할 때 넘기는 좌표 한 점. 순서가 곧 코스 방문 순서다. */
@Serializable
data class CoursePlaceCoordinate(
    val latitude: Double,
    val longitude: Double,
)
