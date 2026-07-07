package com.chillsam.courmy.common.data.mediaSearch

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.common.data.mediaSearch.dto.ImageSearchResponse
import com.chillsam.courmy.common.data.mediaSearch.dto.VideoSearchResponse

class MediaSearchDataSource(private val apiService: MediaSearchApiService) : BaseRemoteDataSource() {

    suspend fun searchImages(query: String, page: Int): ImageSearchResponse {
        return checkResponse(apiService.searchImages(query = query, page = page))
    }

    suspend fun searchVideos(query: String, page: Int): VideoSearchResponse {
        return checkResponse(apiService.searchVideos(query = query, page = page))
    }
}
