package com.chillsam.courmy.main.data.my

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.my.dto.MyProfileEnvelope

class MyProfileDataSource(
    private val apiService: MyProfileApiService,
) : BaseRemoteDataSource() {
    suspend fun getMyProfile(): MyProfileEnvelope = checkResponse(apiService.getMyProfile())
}
