package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.search.dto.CourseSearchEnvelope

class CourseSearchDataSource(
    private val apiService: CourseSearchApiService,
) : BaseRemoteDataSource() {
    suspend fun searchCourses(
        keyword: String,
        size: Int,
        cursor: String?,
    ): CourseSearchEnvelope = checkResponse(apiService.searchCourses(keyword = keyword, size = size, cursor = cursor))
}
