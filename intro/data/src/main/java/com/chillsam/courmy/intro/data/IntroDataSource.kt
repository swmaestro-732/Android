package com.chillsam.courmy.intro.data

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.intro.data.dto.IntroDTO

class IntroDataSource(private val introApiService: IntroApiService) : BaseRemoteDataSource() {
    suspend fun getIntro(): IntroDTO {
        return checkResponse(introApiService.getIntro())
    }
}
