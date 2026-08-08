package com.chillsam.courmy.common.data.auth

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/** POST /api/v1/auth/refresh 요청. */
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
    @POST("api/v1/auth/refresh")
    fun reissue(
        @Body request: TokenReissueRequest,
    ): Call<TokenReissueEnvelope>
}
