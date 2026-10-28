package com.chillsam.courmy.main.domain.auth

import javax.inject.Inject

/** 저장된 세션을 복원하고 로그인 상태를 돌려준다(스플래시 자동 로그인). */
class RestoreSessionUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke(): Boolean {
            repository.restoreSession()
            return repository.isLoggedIn
        }
    }
