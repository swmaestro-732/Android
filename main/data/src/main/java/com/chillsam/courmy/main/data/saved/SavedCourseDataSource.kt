package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.saved.dto.SavedCourseEnvelope
import com.chillsam.courmy.main.data.saved.dto.SavedCourseIdsEnvelope

class SavedCourseDataSource(
    private val apiService: SavedCourseApiService,
) : BaseRemoteDataSource() {
    suspend fun getSavedCourses(
        size: Int,
        cursor: String?,
    ): SavedCourseEnvelope = checkResponse(apiService.getSavedCourses(size, cursor))

    suspend fun getSavedCourseIds(
        size: Int,
        cursor: String?,
    ): SavedCourseIdsEnvelope = checkResponse(apiService.getSavedCourseIds(size, cursor))
}
