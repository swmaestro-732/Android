package com.chillsam.courmy.common.data.auth

import com.chillsam.courmy.common.domain.session.SessionEventBus
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * accessToken 만료(401) 시 refreshToken 으로 토큰을 재발급받아 원요청을 1회 재시도한다.
 * 재발급 자체가 실패(refresh 만료 등)하면 로컬 세션을 비우고 [SessionEventBus] 로 만료를 알려
 * presentation 이 로그인 화면으로 유도하게 한다.
 */
@Singleton
class TokenAuthenticator
    @Inject
    constructor(
        private val tokenStore: TokenStore,
        private val reissueApi: TokenReissueApi,
        private val sessionEventBus: SessionEventBus,
    ) : Authenticator {
        private val lock = Any()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            val refresh = refreshTokenIfRetryable(response) ?: return null
            val failedToken =
                response.request
                    .header("Authorization")
                    ?.removePrefix("Bearer ")
                    ?.trim()
            val newToken = synchronized(lock) { resolveToken(failedToken, refresh) }
            return newToken?.let {
                response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
            }
        }

        /**
         * 재시도 가능한 401 이면 사용할 refreshToken 을, 아니면 null 을 돌려준다.
         * - 재발급 호출 자체의 401 은 제외(무한 재귀 방지), 이미 한 번 재시도한 요청도 제외(무한 루프 방지).
         */
        private fun refreshTokenIfRetryable(response: Response): String? {
            val retryable =
                !response.request.url.encodedPath
                    .endsWith(REISSUE_PATH) &&
                    priorResponseCount(response) < 1
            if (!retryable) return null
            return tokenStore.refreshToken?.takeIf { it.isNotBlank() }
        }

        /**
         * 재시도에 쓸 accessToken 을 정한다. 다른 요청이 먼저 재발급을 마쳤으면 그 토큰을, 아니면 직접
         * 재발급한다. 재발급까지 실패하면 세션을 비우고 만료를 알린 뒤 null(=재시도 포기).
         */
        private fun resolveToken(
            failedToken: String?,
            refresh: String,
        ): String? {
            val current = tokenStore.accessToken
            if (current != null && current != failedToken) return current
            return reissue(refresh) ?: run {
                tokenStore.clear()
                sessionEventBus.notifyExpired()
                null
            }
        }

        /** 재발급 성공 시 새 accessToken 을 저장하고 돌려준다. 실패면 null. */
        private fun reissue(refreshToken: String): String? {
            val data =
                runCatching { reissueApi.reissue(TokenReissueRequest(refreshToken)).execute() }
                    .getOrNull()
                    ?.takeIf { it.isSuccessful }
                    ?.body()
                    ?.data
            val access = data?.accessToken ?: return null
            tokenStore.updateSession(access, data.refreshToken ?: refreshToken)
            return access
        }

        private fun priorResponseCount(response: Response): Int {
            var count = 0
            var prior = response.priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }

        private companion object {
            const val REISSUE_PATH = "api/v1/auth/token-reissue"
        }
    }
