package com.chillsam.courmy.main.domain.onboarding

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import javax.inject.Inject

/** 온보딩을 완료 처리한다(시작/건너뛰기 시 호출). */
class CompleteOnboardingUseCase
    @Inject
    constructor(
        private val repository: OnboardingRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke() = telemetry.track(AppFlow.Onboarding) { repository.setOnboarded() }
    }
