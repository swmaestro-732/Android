package com.chillsam.courmy.course.data.draft

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.draft.dto.DraftListEnvelope

class DraftDataSource(
    private val apiService: DraftApiService,
) : BaseRemoteDataSource() {
    suspend fun getDrafts(): DraftListEnvelope = checkResponse(apiService.getDrafts())
}
