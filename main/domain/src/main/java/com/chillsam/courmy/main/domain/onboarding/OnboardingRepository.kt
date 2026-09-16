package com.chillsam.courmy.main.domain.onboarding

/** 온보딩 완료 여부(최초 실행 판별)를 로컬에 보관한다. */
interface OnboardingRepository {
    /** 온보딩을 이미 마쳤으면 true. */
    suspend fun isOnboarded(): Boolean

    /** 온보딩 완료로 표시(다음 실행부터 온보딩을 건너뛴다). */
    suspend fun setOnboarded()
}
