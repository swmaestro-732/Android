package com.chillsam.courmy.course.data.directions

import com.chillsam.courmy.course.domain.DirectionsRepository
import com.chillsam.courmy.course.entity.CoursePlaceCoordinate

class DirectionsRepositoryImpl(
    private val dataSource: DirectionsDataSource,
) : DirectionsRepository {
    override suspend fun walkingMinutes(points: List<CoursePlaceCoordinate>): List<Int> =
        dataSource.walkingMinutes(points)
}
