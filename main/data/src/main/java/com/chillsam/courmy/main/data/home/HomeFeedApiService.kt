package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.main.data.home.dto.CourseFeedEnvelope
import com.chillsam.courmy.main.data.home.dto.HomeEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeFeedApiService {
    /**
     * 추천 코스 전체 목록 v2(BFF). 홈 미리보기(`service/v2/home`)와 같은 추천 랭킹을 커서로 이어 받는다.
     * 비로그인도 조회할 수 있는 공개 엔드포인트다. 세로 피드 전체(전체보기)가 쓴다.
     * 응답 모양은 구 `service/v1/home` 과 같아(courses·nextCursor·hasNext) DTO 를 공유한다.
     */
    @GET("service/v2/home/recommended-courses")
    suspend fun getCourseFeed(
        @Query("size") size: Int,
        /** 이전 응답의 `nextCursor`. 첫 페이지면 null 이며, null 이면 Retrofit 이 파라미터를 뺀다. */
        @Query("cursor") cursor: String? = null,
        /**
         * 서버 CourseCategory enum 이름(예: `DATE`). null 이면 전체 카테고리이고,
         * 알 수 없는 값은 400(4001)으로 거절되므로 enum `name` 외의 값을 보내지 않는다.
         */
        @Query("category") category: String? = null,
    ): Response<CourseFeedEnvelope>

    /**
     * 홈 화면 조합 v2(BFF) — 추천 코스 미리보기 + 근처 저장 장소.
     *
     * 공개 엔드포인트지만 근처 저장 장소는 JWT 로 사용자를 식별해 내려준다(비로그인이면 빈 배열).
     * 스펙의 `viewerId` 쿼리는 springdoc 오표기로, 실제로는 JWT 가 정본이라 보내지 않는다.
     */
    @GET("service/v2/home")
    suspend fun getHome(
        /** 근처 저장 장소의 거리·도보 시간 기준점. 없으면 서버 기본값을 따른다. */
        @Query("userLat") userLat: Double? = null,
        @Query("userLng") userLng: Double? = null,
    ): Response<HomeEnvelope>
}
