package com.chillsam.courmy.common.data.auth

import com.chillsam.courmy.common.domain.auth.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SessionRepository] 구현. 세션 판단을 [TokenStore] 한 곳에 맡긴다.
 *
 * 값을 복사해 두지 않고 매번 물어본다 — 세션 만료·로그아웃으로 토큰이 비워지면
 * 다음 조회부터 바로 반영돼야 한다.
 */
@Singleton
class SessionRepositoryImpl
    @Inject
    constructor(
        private val tokenStore: TokenStore,
    ) : SessionRepository {
        override val isLoggedIn: Boolean
            get() = tokenStore.isLoggedIn
    }
