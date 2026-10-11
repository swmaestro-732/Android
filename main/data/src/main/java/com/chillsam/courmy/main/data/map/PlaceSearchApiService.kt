package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.data.map.dto.ExternalMapPlaceSearchEnvelope
import com.chillsam.courmy.main.data.map.dto.MapPlaceSearchEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceSearchApiService {
    @GET("api/v1/places/search")
    suspend fun search(
        @Query("query") query: String,
    ): Response<ExternalMapPlaceSearchEnvelope>

    /** 지도 탭의 검색어 없는 목록 보기. 검색 요청과 분리한다. */
    @GET("api/v1/places")
    suspend fun getPlaces(
        @Query("cursor") cursor: String?,
        @Query("size") size: Int = 20,
    ): Response<MapPlaceSearchEnvelope>
}
