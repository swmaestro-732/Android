package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.MapPlaceSearchEnvelope
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceSearchApiService {
    @GET("api/v1/places")
    suspend fun suggestions(
        @Query("q") query: String,
        @Query("size") size: Int = 10,
    ): Response<MapPlaceSearchEnvelope>

    /** 지도 검색은 표시 영역 안의 등록 장소와 클러스터를 조회한다. */
    @GET("api/v1/places/map")
    suspend fun search(
        @Query("q") query: String,
        @Query("swLat") swLat: Double,
        @Query("swLng") swLng: Double,
        @Query("neLat") neLat: Double,
        @Query("neLng") neLng: Double,
        /** 서버 요청 enum 이름(예: "CAFE"). null 이면 전체([PlaceMapApiService] 와 같은 계약). */
        @Query("category") category: String? = null,
        @Query("userLat") latitude: Double? = null,
        @Query("userLng") longitude: Double? = null,
    ): Response<PlaceMapEnvelope>
}
