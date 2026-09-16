package com.chillsam.courmy.common.data.auth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세션 토큰 보관소.
 *
 * OkHttp 인증 인터셉터/[TokenAuthenticator] 가 [accessToken]·[refreshToken] 을 동기적으로 읽으므로
 * 값은 @Volatile 인메모리 캐시로 노출한다. 동시에 access/refresh 는 [SecureTokenStorage](Keystore
 * 암호화 + DataStore)에 write-through 로 영속화해, 콜드 스타트에도 [load] 로 세션을 복원(자동 로그인)한다.
 *
 * registrationToken 은 가입 절차 중에만 쓰는 임시값이라 영속화하지 않는다(프로세스 생존 동안만).
 */
@Singleton
class TokenStore
    @Inject
    constructor(
        private val secureStorage: SecureTokenStorage,
    ) {
        // 암호화 저장(IO)을 백그라운드로 수행하는 앱 수명 스코프.
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @Volatile
        var accessToken: String? = null
            private set

        @Volatile
        var refreshToken: String? = null
            private set

        /** 현재 사용자 id. accessToken(JWT)의 sub 에서 파생되며, 토큰과 함께 채워지고 비워진다. */
        @Volatile
        var userId: Long? = null
            private set

        /** 신규 회원가입에 필요한 임시 토큰(social-login 시 newUser 응답으로 받음). 영속화하지 않는다. */
        @Volatile
        var registrationToken: String? = null
            private set

        val isLoggedIn: Boolean
            get() = accessToken != null

        /**
         * 콜드 스타트 시 암호화 저장소에서 세션을 복원해 인메모리 캐시를 채운다(자동 로그인).
         * 복호화 실패(재설치·기기 복원 등)면 조용히 세션 없음으로 둔다.
         */
        suspend fun load() {
            val tokens = runCatching { secureStorage.read() }.getOrNull()
            accessToken = tokens?.accessToken
            refreshToken = tokens?.refreshToken
            userId = userIdFromAccessToken(tokens?.accessToken)
        }

        /** 로그인/가입/재발급 성공 시 세션 토큰을 저장한다. registrationToken 은 더 필요 없으므로 비운다. */
        fun updateSession(
            accessToken: String,
            refreshToken: String,
        ) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
            this.userId = userIdFromAccessToken(accessToken)
            this.registrationToken = null
            scope.launch { runCatching { secureStorage.write(accessToken, refreshToken) } }
        }

        fun updateRegistrationToken(token: String) {
            this.registrationToken = token
        }

        fun clear() {
            accessToken = null
            refreshToken = null
            userId = null
            registrationToken = null
            scope.launch { runCatching { secureStorage.clear() } }
        }
    }
