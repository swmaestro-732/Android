package com.chillsam.courmy.main.domain.auth

import javax.inject.Inject

/** 회원 탈퇴 UseCase. 서버 계정을 삭제하고 로컬 토큰을 제거한다. */
class WithdrawUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke(userId: Long) = repository.withdraw(userId)
    }
