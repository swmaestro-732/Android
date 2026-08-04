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

    /**
     * 회원 탈퇴. 기본 경로가 "경로 없음"으로 실패하면 develop 의 새 경로로 한 번 더 시도한다
     * ([AuthApiService.withdrawFallback] 참고). 401 등 다른 실패는 그대로 예외로 올린다.
     */
    suspend fun withdraw(userId: Long): SimpleEnvelope {
        val primary = apiService.withdraw(userId)
        if (!primary.isSuccessful && primary.code() in PATH_MISSING_CODES) {
            return checkResponse(apiService.withdrawFallback())
        }
        return checkResponse(primary)
    }

    suspend fun checkHandleAvailability(handle: String): Boolean =
        checkResponse(apiService.checkHandleAvailability(handle)).data?.available ?: false

    private companion object {
        /** 경로가 없다고 판단할 상태 코드. 미매핑 경로에 500 을 주는 서버 동작을 포함. */
        val PATH_MISSING_CODES = setOf(404, 405, 500)
    }
}
