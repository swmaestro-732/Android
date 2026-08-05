package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.data.saved.dto.SavedCourseEnvelope
import com.chillsam.courmy.main.data.saved.dto.SavedCourseIdsEnvelope
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SavedCourseApiService {
    /**
     * 저장함 · 코스 탭 화면 조합(BFF). 사용자는 JWT 로 식별한다.
     * 폴더·완주 필터와 커서 페이징도 지원하지만, 화면에 해당 UI 가 생기면 파라미터를 추가한다.
     */
    @GET("service/v1/my/saved-courses")
    suspend fun getSavedCourses(
        @Query("size") size: Int,
    ): Response<SavedCourseEnvelope>

    /**
     * 저장 레코드 목록(도메인 API) — 코스 요약 없이 id 만 온다.
     *
     * 홈 피드 응답에 저장 여부가 없어, 카드 아이콘을 채우려면 이 목록과 대조해야 한다.
     * TODO-API-SPEC: 피드 응답에 `hasSaved` 가 추가되면 이 호출과 대조 로직을 제거한다. [wiki-needed]
     */
    @GET("api/v1/my/saved-courses")
    suspend fun getSavedCourseIds(
        @Query("size") size: Int,
    ): Response<SavedCourseIdsEnvelope>
}
