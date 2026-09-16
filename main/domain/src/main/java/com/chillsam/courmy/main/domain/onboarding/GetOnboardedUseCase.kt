package com.chillsam.courmy.main.domain.onboarding

import javax.inject.Inject

/** 온보딩 완료 여부를 조회한다(스플래시에서 온보딩/홈 분기용). */
class GetOnboardedUseCase
    @Inject
    constructor(
        private val repository: OnboardingRepository,
    ) {
        suspend operator fun invoke(): Boolean = repository.isOnboarded()
    }
