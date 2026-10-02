package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceMapApiService {
    /**
     * 지도 영역(남서·북동 꼭짓점) 안의 장소 탐색. 비로그인도 조회할 수 있는 공개 엔드포인트다.
     * null 파라미터는 Retrofit 이 쿼리에서 뺀다(서버 기본값: sort=RELEVANCE, category=전체).
     */
    @GET("api/v1/places/map")
    suspend fun getMapPlaces(
        @Query("swLat") swLat: Double,
        @Query("swLng") swLng: Double,
        @Query("neLat") neLat: Double,
        @Query("neLng") neLng: Double,
        /** 서버 `PlaceCategory` enum 이름(예: "CAFE"). */
        @Query("category") category: String? = null,
        /** "RELEVANCE" | "DISTANCE". DISTANCE 는 userLat/userLng 기준이다. */
        @Query("sort") sort: String? = null,
        @Query("userLat") userLat: Double? = null,
        @Query("userLng") userLng: Double? = null,
    ): Response<PlaceMapEnvelope>
}
