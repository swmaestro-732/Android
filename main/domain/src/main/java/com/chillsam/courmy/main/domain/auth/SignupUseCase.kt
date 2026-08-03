package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.main.entity.auth.SignupProfile
import javax.inject.Inject

/** 회원가입 UseCase. 저장된 registrationToken + 입력 프로필로 가입을 완료한다. */
class SignupUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke(profile: SignupProfile) = repository.signup(profile)
    }
