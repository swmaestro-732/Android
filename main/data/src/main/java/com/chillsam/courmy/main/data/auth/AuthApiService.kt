package com.chillsam.courmy.main.data.auth

import com.chillsam.courmy.main.data.auth.dto.AvailabilityEnvelope
import com.chillsam.courmy.main.data.auth.dto.LogoutRequest
import com.chillsam.courmy.main.data.auth.dto.SignupEnvelope
import com.chillsam.courmy.main.data.auth.dto.SignupRequest
import com.chillsam.courmy.main.data.auth.dto.SimpleEnvelope
import com.chillsam.courmy.main.data.auth.dto.SocialLoginEnvelope
import com.chillsam.courmy.main.data.auth.dto.SocialLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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

    /**
     * 회원 탈퇴 폴백. 백엔드 develop 이 탈퇴를 `DELETE /api/v1/users`(대상은 JWT 의 나)로 옮겨,
     * 배포 시점에 따라 둘 중 하나만 살아 있다.
     *
     * TODO-API-SPEC: 경로가 하나로 확정되면 나머지 하나와 폴백 분기를 제거한다. [wiki-needed]
     */
    @DELETE("api/v1/users")
    suspend fun withdrawFallback(): Response<SimpleEnvelope>

    /**
     * 아이디(핸들) 사용 가능 여부. 예약어이거나 이미 사용 중이면 available=false.
     * 구 경로 `api/v1/auth/login-id/availability` 는 백엔드에서 Deprecated 로 표시돼 이쪽을 쓴다.
     */
    @GET("api/v1/users/availability")
    suspend fun checkHandleAvailability(
        @Query("handle") handle: String,
    ): Response<AvailabilityEnvelope>
}
