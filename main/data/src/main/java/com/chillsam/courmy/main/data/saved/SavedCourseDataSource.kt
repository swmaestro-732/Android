package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.saved.dto.SavedCourseEnvelope

class SavedCourseDataSource(
    private val apiService: SavedCourseApiService,
) : BaseRemoteDataSource() {
    suspend fun getSavedCourses(size: Int): SavedCourseEnvelope = checkResponse(apiService.getSavedCourses(size))
}
