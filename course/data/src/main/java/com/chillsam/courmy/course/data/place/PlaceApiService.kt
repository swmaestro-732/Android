package com.chillsam.courmy.course.data.place

import com.chillsam.courmy.course.data.place.dto.ExternalPlaceSearchEnvelope
import com.chillsam.courmy.course.data.place.dto.PlaceSearchEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceApiService {
    /**
     * 장소 검색. 검색어 파라미터 이름은 `query` 가 아니라 **`q`** 다.
     *
     * 백엔드 develop 은 `searchByName(q, cursor, size)` 로 필터링하지만, 배포된 구버전은 `q` 를
     * 무시하고 등록된 장소를 전부 돌려준다. 배포되면 별도 수정 없이 필터가 동작한다.
     */
    @GET("api/v1/places")
    suspend fun searchPlaces(
        @Query("q") query: String,
        @Query("size") size: Int = DEFAULT_SIZE,
    ): Response<PlaceSearchEnvelope>

    /**
     * 외부 지도(카카오 로컬) 장소 검색. 검색어 파라미터는 `query` 다(`q` 가 아니다).
     *
     * 등록된 장소만 도는 [searchPlaces] 와 달리 실제 지도 데이터를 찾아 주고,
     * 서버가 결과를 내부 저장(dedup)해 우리 place id 를 함께 내려준다.
     */
    @GET("api/v1/places/search")
    suspend fun searchExternalPlaces(
        @Query("query") query: String,
    ): Response<ExternalPlaceSearchEnvelope>

    companion object {
        const val DEFAULT_SIZE = 20
    }
}
