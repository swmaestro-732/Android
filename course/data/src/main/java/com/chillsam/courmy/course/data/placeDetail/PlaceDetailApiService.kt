package com.chillsam.courmy.course.data.placeDetail

import com.chillsam.courmy.course.data.placeDetail.dto.PlaceDetailEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlaceDetailApiService {
    /**
     * 장소 상세 화면 조합(BFF).
     *
     * TODO-API-SPEC: 백엔드가 아직 목이라 `placeId=101` 만 200 이고 나머지는 404(PLACE_NOT_FOUND)다
     * (`PlaceDetailScreenController` 에 주입 의존성이 없다). 실제 조회로 교체되면 그대로 동작한다. [wiki-needed]
     */
    @GET("service/v1/places/{placeId}")
    suspend fun getPlaceDetail(
        @Path("placeId") placeId: Long,
    ): Response<PlaceDetailEnvelope>
}
