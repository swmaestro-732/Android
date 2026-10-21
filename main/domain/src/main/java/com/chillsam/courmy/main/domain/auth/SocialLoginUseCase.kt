package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import com.chillsam.courmy.main.entity.auth.SocialLoginResult
import com.chillsam.courmy.main.entity.auth.SocialProvider
import javax.inject.Inject

/** 소셜 로그인 UseCase. 소셜 idToken 을 서버에 넘겨 로그인/가입 분기 결과를 얻는다. */
class SocialLoginUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            provider: SocialProvider,
            idToken: String,
        ): SocialLoginResult =
            telemetry.track(AppFlow.Login) {
                repository.socialLogin(provider, idToken)
            }
    }
