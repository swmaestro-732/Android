package com.chillsam.courmy.main.data.auth

import com.chillsam.courmy.main.data.auth.dto.LogoutRequest
import com.chillsam.courmy.main.data.auth.dto.SignupEnvelope
import com.chillsam.courmy.main.data.auth.dto.SignupRequest
import com.chillsam.courmy.main.data.auth.dto.SimpleEnvelope
import com.chillsam.courmy.main.data.auth.dto.SocialLoginEnvelope
import com.chillsam.courmy.main.data.auth.dto.SocialLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    /** 소셜 로그인(+신규 판별). baseUrl 은 common:data 의 NetworkModule 이 제공. */
    @POST("api/v1/auth/social-login")
    suspend fun socialLogin(
        @Body request: SocialLoginRequest,
    ): Response<SocialLoginEnvelope>

    /** 회원가입 완료. */
    @POST("api/v1/auth/signup")
    suspend fun signup(
        @Body request: SignupRequest,
    ): Response<SignupEnvelope>

    /** 로그아웃(서버 세션·refresh 토큰 무효화). Bearer 필요. */
    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest,
    ): Response<SimpleEnvelope>

    /** 회원 탈퇴. userId 는 accessToken(JWT sub)에서 얻어 쿼리로 전달한다. */
    @DELETE("api/v1/my")
    suspend fun withdraw(
        @Query("userId") userId: Long,
    ): Response<SimpleEnvelope>
}
