package com.chillsam.courmy.course.data.directions

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.directions.dto.WalkingDirectionsRequest
import com.chillsam.courmy.course.data.directions.dto.WalkingPointDTO
import com.chillsam.courmy.course.entity.CoursePlaceCoordinate

class DirectionsDataSource(
    private val apiService: DirectionsApiService,
) : BaseRemoteDataSource() {
    suspend fun walkingMinutes(points: List<CoursePlaceCoordinate>): List<Int> {
        val request =
            WalkingDirectionsRequest(
                points = points.map { WalkingPointDTO(lat = it.latitude, lng = it.longitude) },
            )
        return checkResponse(apiService.walking(request)).data?.segments.orEmpty()
    }
}
