package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.data.saved.dto.SavedPlaceEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SavedPlaceApiService {
    /**
     * 저장함 · 장소 탭 화면 조합(BFF). 사용자는 JWT 로 식별한다.
     * 카테고리 필터·현재 위치 기반 도보 시간도 지원하지만, 화면에 해당 UI 가 생기면 파라미터를 추가한다.
     */
    @GET("service/v1/my/saved-places")
    suspend fun getSavedPlaces(
        @Query("size") size: Int,
        /** 이전 응답의 `nextCursor`. null 이면 Retrofit 이 파라미터를 뺀다(첫 페이지). */
        @Query("cursor") cursor: String? = null,
        /** 방문 여부 필터. null 이면 전체. */
        @Query("visited") visited: Boolean? = null,
    ): Response<SavedPlaceEnvelope>
}
