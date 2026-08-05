package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.saved.dto.SavedCourseEnvelope
import com.chillsam.courmy.main.data.saved.dto.SavedCourseIdsEnvelope

class SavedCourseDataSource(
    private val apiService: SavedCourseApiService,
) : BaseRemoteDataSource() {
    suspend fun getSavedCourses(size: Int): SavedCourseEnvelope = checkResponse(apiService.getSavedCourses(size))

    suspend fun getSavedCourseIds(size: Int): SavedCourseIdsEnvelope = checkResponse(apiService.getSavedCourseIds(size))
}
