package com.chillsam.courmy.common.data.auth

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/** POST /api/v1/auth/token-reissue 요청. */
@Serializable
data class TokenReissueRequest(
    val refreshToken: String,
)

/** 공통 응답 봉투 { code, message, data, fieldErrors }. */
@Serializable
data class TokenReissueEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: TokenResponseDTO? = null,
)

@Serializable
data class TokenResponseDTO(
    val accessToken: String? = null,
    val refreshToken: String? = null,
)

/**
 * 토큰 재발급 전용 서비스. [TokenAuthenticator] 가 OkHttp 콜백 스레드에서 동기로 호출하므로
 * suspend 대신 [Call] 을 노출한다(`.execute()` 블로킹). 인증 인터셉터·Authenticator 가 붙지 않은
 * 별도(bare) Retrofit 으로 생성해 재발급 요청이 다시 401→재발급으로 재귀하지 않게 한다.
 */
interface TokenReissueApi {
    @POST("api/v1/auth/token-reissue")
    fun reissue(
        @Body request: TokenReissueRequest,
    ): Call<TokenReissueEnvelope>

    /**
     * 재발급 경로 폴백.
     *
     * 백엔드 develop 이 같은 기능을 `POST /api/v1/auth/refresh` 로 옮겨 둬, 배포 시점에 따라
     * 둘 중 하나만 살아 있다. 재발급이 실패하면 토큰 만료 시점에 전원 강제 로그아웃으로 이어지므로,
     * [reissue] 가 "경로 없음"으로 실패했을 때만 이쪽을 한 번 더 시도한다([TokenAuthenticator]).
     *
     * TODO-API-SPEC: 백엔드 경로가 하나로 확정되면 나머지 하나와 폴백 분기를 제거한다. [wiki-needed]
     */
    @POST("api/v1/auth/refresh")
    fun reissueFallback(
        @Body request: TokenReissueRequest,
    ): Call<TokenReissueEnvelope>
}
