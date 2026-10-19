package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.main.data.search.dto.CourseSearchEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseSearchApiService {
    /**
     * 코스 통합 검색. 비로그인도 조회할 수 있는 공개 엔드포인트다.
     * `area`·`category`·`tags`·`sort` 파라미터도 받지만 화면(FS-18)에 해당 필터가 없어 싣지 않는다.
     */
    @GET("api/v1/courses/search")
    suspend fun searchCourses(
        @Query("keyword") keyword: String,
        @Query("size") size: Int,
        /** 이전 응답의 `nextCursor`. null 이면 Retrofit 이 파라미터를 뺀다(첫 페이지). */
        @Query("cursor") cursor: String? = null,
    ): Response<CourseSearchEnvelope>
}
