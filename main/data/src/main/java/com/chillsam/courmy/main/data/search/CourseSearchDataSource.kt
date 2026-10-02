package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.search.dto.CourseSearchEnvelope

class CourseSearchDataSource(
    private val apiService: CourseSearchApiService,
) : BaseRemoteDataSource() {
    /**
     * 코스 검색.
     *
     * [CourseSearchFallback] 은 디버그 빌드에서 **요청이 실패했을 때만** 예시 응답으로 받아 주고,
     * 릴리스에서는 그대로 통과시켜 예외가 화면 에러로 올라간다. 빈 결과는 정상 응답이라 덮지 않는다.
     */
    suspend fun searchCourses(
        keyword: String,
        size: Int,
    ): CourseSearchEnvelope =
        CourseSearchFallback.resolve {
            checkResponse(apiService.searchCourses(keyword = keyword, size = size))
        }
}
