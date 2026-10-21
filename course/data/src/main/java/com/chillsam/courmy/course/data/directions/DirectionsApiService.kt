package com.chillsam.courmy.course.data.directions

import com.chillsam.courmy.course.data.directions.dto.WalkingDirectionsEnvelope
import com.chillsam.courmy.course.data.directions.dto.WalkingDirectionsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** 장소 사이 도보 시간. 코스 생성에서 장소 목록이 바뀔 때마다 다시 묻는다. */
interface DirectionsApiService {
    @POST("api/v1/directions/walking")
    suspend fun walking(
        @Body request: WalkingDirectionsRequest,
    ): Response<WalkingDirectionsEnvelope>
}
