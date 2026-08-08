package com.chillsam.courmy.main.data.auth

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.auth.dto.LogoutRequest
import com.chillsam.courmy.main.data.auth.dto.SignupEnvelope
import com.chillsam.courmy.main.data.auth.dto.SignupRequest
import com.chillsam.courmy.main.data.auth.dto.SimpleEnvelope
import com.chillsam.courmy.main.data.auth.dto.SocialLoginEnvelope
import com.chillsam.courmy.main.data.auth.dto.SocialLoginRequest

class AuthDataSource(
    private val apiService: AuthApiService,
) : BaseRemoteDataSource() {
    suspend fun socialLogin(request: SocialLoginRequest): SocialLoginEnvelope =
        checkResponse(apiService.socialLogin(request))

    suspend fun signup(request: SignupRequest): SignupEnvelope = checkResponse(apiService.signup(request))

    suspend fun logout(request: LogoutRequest): SimpleEnvelope = checkResponse(apiService.logout(request))

    suspend fun withdraw(): SimpleEnvelope = checkResponse(apiService.withdraw())

    suspend fun checkHandleAvailability(handle: String): Boolean =
        checkResponse(apiService.checkHandleAvailability(handle)).data?.available ?: false
}
