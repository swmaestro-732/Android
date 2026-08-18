package com.chillsam.courmy.main.domain.auth

import javax.inject.Inject

/** 로그아웃 UseCase. 서버 세션 무효화(best-effort) 후 로컬 토큰을 제거한다. */
class LogoutUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke() = repository.logout()
    }
