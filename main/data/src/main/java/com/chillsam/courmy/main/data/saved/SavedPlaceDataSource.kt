package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.saved.dto.SavedPlaceEnvelope

class SavedPlaceDataSource(
    private val apiService: SavedPlaceApiService,
) : BaseRemoteDataSource() {
    suspend fun getSavedPlaces(
        size: Int,
        cursor: String?,
        visited: Boolean?,
    ): SavedPlaceEnvelope = checkResponse(apiService.getSavedPlaces(size, cursor, visited))
}
