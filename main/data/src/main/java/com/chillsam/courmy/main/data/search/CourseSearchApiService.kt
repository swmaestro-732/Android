package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.main.data.search.dto.CourseSearchEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseSearchApiService {
    /**
     * 코스 통합 검색. 비로그인도 조회할 수 있는 공개 엔드포인트다.
     *
     * `area` 는 코스의 표시 지역명 그대로(예: "서울" — 코드·시군구명은 0건), `category` 는 대문자 enum
     * 이름(예: "FOOD"), `sort` 는 RELEVANCE·LATEST·POPULAR 다(2026-11-07 실호출 확인). `tags` 는
     * 화면에 필터가 없어 싣지 않는다. null 파라미터는 Retrofit 이 뺀다.
     */
    @GET("api/v1/courses/search")
    suspend fun searchCourses(
        @Query("keyword") keyword: String,
        @Query("size") size: Int,
        /** 이전 응답의 `nextCursor`. null 이면 Retrofit 이 파라미터를 뺀다(첫 페이지). */
        @Query("cursor") cursor: String? = null,
        @Query("area") area: String? = null,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null,
    ): Response<CourseSearchEnvelope>
}
