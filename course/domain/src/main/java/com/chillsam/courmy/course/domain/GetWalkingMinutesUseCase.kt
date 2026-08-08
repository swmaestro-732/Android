package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CoursePlaceCoordinate
import javax.inject.Inject

/**
 * 코스에 담은 장소 사이의 도보 시간(분)을 구간별로 구한다.
 *
 * 좌표가 2개 미만이면 구간이 없으므로 서버를 부르지 않고 빈 목록을 돌려준다.
 */
class GetWalkingMinutesUseCase
    @Inject
    constructor(
        private val repository: DirectionsRepository,
    ) {
        suspend operator fun invoke(points: List<CoursePlaceCoordinate>): List<Int> =
            if (points.size < MIN_POINTS) emptyList() else repository.walkingMinutes(points)

        private companion object {
            const val MIN_POINTS = 2
        }
    }
