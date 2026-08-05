package com.chillsam.courmy.common.data.auth

import com.chillsam.courmy.common.domain.session.SessionEventBus
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
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
            val path = response.request.url.encodedPath
            val retryable =
                REISSUE_PATHS.none { path.endsWith(it) } &&
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

        /**
         * 재발급 성공 시 새 accessToken 을 저장하고 돌려준다. 실패면 null.
         *
         * 기본 경로가 "경로 없음"으로 실패하면 폴백 경로로 한 번 더 시도한다
         * (백엔드가 재발급 경로를 옮기는 중 — [TokenReissueApi.reissueFallback] 참고).
         * 401/400 은 refreshToken 자체가 무효라는 뜻이므로 폴백하지 않는다.
         */
        private fun reissue(refreshToken: String): String? {
            val request = TokenReissueRequest(refreshToken)
            val primary = call { reissueApi.reissue(request) }
            val data =
                primary.data
                    ?: if (primary.pathMissing) call { reissueApi.reissueFallback(request) }.data else null
            val access = data?.accessToken ?: return null
            tokenStore.updateSession(access, data.refreshToken ?: refreshToken)
            return access
        }

        private fun call(request: () -> Call<TokenReissueEnvelope>): ReissueAttempt {
            val response = runCatching { request().execute() }.getOrNull()
            return when {
                response == null -> ReissueAttempt()

                response.isSuccessful -> ReissueAttempt(data = response.body()?.data)

                // 이 서버는 매핑되지 않은 경로에도 500 을 돌려주므로 404/405 와 함께 "경로 없음" 후보로 본다.
                else -> ReissueAttempt(pathMissing = response.code() in PATH_MISSING_CODES)
            }
        }

        private data class ReissueAttempt(
            val data: TokenResponseDTO? = null,
            val pathMissing: Boolean = false,
        )

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
            /** 재발급 호출 자체가 다시 재발급을 유발하지 않도록 제외할 경로(폴백 포함). */
            val REISSUE_PATHS = listOf("api/v1/auth/token-reissue", "api/v1/auth/refresh")

            /** 경로가 없다고 판단할 상태 코드. 404/405 외에 미매핑 경로에 500 을 주는 서버 동작을 포함. */
            val PATH_MISSING_CODES = setOf(404, 405, 500)
        }
    }
