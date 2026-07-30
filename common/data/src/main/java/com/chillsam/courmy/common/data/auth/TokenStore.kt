package com.chillsam.courmy.common.data.auth

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세션 토큰 보관소(인메모리).
 *
 * access token 등 인증정보는 평문 Preferences 에 저장하지 않는다는 규약에 따라, 우선 프로세스
 * 생존 동안만 메모리에 보관한다(콜드 스타트 시 초기화 = 재로그인 필요). 보안 저장소(Encrypted
 * SharedPreferences 등) 도입 시 이 클래스 내부만 교체한다.
 *
 * OkHttp 인증 인터셉터가 [accessToken] 을 동기적으로 읽으므로 값은 @Volatile 로 노출한다.
 */
@Singleton
class TokenStore
    @Inject
    constructor() {
        @Volatile
        var accessToken: String? = null
            private set

        @Volatile
        var refreshToken: String? = null
            private set

        /** 신규 회원가입에 필요한 임시 토큰(social-login 시 newUser 응답으로 받음). */
        @Volatile
        var registrationToken: String? = null
            private set

        val isLoggedIn: Boolean
            get() = accessToken != null

        /** 로그인/가입 성공 시 세션 토큰을 저장한다. registrationToken 은 더 필요 없으므로 비운다. */
        fun updateSession(
            accessToken: String,
            refreshToken: String,
        ) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
            this.registrationToken = null
        }

        fun updateRegistrationToken(token: String) {
            this.registrationToken = token
        }

        fun clear() {
            accessToken = null
            refreshToken = null
            registrationToken = null
        }
    }
