package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.main.data.home.dto.CourseFeedEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeFeedApiService {
    /**
     * 공개 코스 피드 화면 조합(BFF). 저장수 내림차순·최신순으로 랭킹된 목록을 준다.
     * 비로그인도 조회할 수 있는 공개 엔드포인트다.
     */
    @GET("service/v1/home")
    suspend fun getCourseFeed(
        @Query("size") size: Int,
        /** 이전 응답의 `nextCursor`. 첫 페이지면 null 이며, null 이면 Retrofit 이 파라미터를 뺀다. */
        @Query("cursor") cursor: String? = null,
    ): Response<CourseFeedEnvelope>
}
