package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.data.saved.dto.SavedCourseEnvelope
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
}
