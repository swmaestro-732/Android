package com.chillsam.courmy.course.data.place

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

    companion object {
        const val DEFAULT_SIZE = 20
    }
}
