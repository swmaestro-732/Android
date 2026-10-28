package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CoursePlaceCoordinate

/**
 * 장소 사이 도보 시간 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 넘긴 좌표가 N 개면 결과는 N-1 개(구간 수)이고 단위는 **분**이다.
 */
interface DirectionsRepository {
    suspend fun walkingMinutes(points: List<CoursePlaceCoordinate>): List<Int>
}
