package com.chillsam.courmy.main.domain.onboarding

import javax.inject.Inject

/** 온보딩을 완료 처리한다(시작/건너뛰기 시 호출). */
class CompleteOnboardingUseCase
    @Inject
    constructor(
        private val repository: OnboardingRepository,
    ) {
        suspend operator fun invoke() = repository.setOnboarded()
    }
