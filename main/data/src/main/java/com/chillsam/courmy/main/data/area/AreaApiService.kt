package com.chillsam.courmy.main.data.area

import com.chillsam.courmy.main.data.area.dto.AreaSearchEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** 행정구역 검색. baseUrl 은 common:data 의 NetworkModule 이 제공한다. */
interface AreaApiService {
    /**
     * 관심 지역 검색.
     *
     * 검색어 파라미터 이름은 `q`·`query` 가 아니라 **`keyword`** 다(서버 `AreaController`).
     * 검색어가 비면 서버가 빈 목록을 주므로 호출부에서 걸러 보내는 편이 낫다.
     */
    @GET("api/v1/areas/search")
    suspend fun searchAreas(
        @Query("keyword") keyword: String,
    ): Response<AreaSearchEnvelope>
}
