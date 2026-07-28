package com.chillsam.courmy.main.data.onboarding

import com.chillsam.courmy.main.domain.onboarding.OnboardingRepository

class OnboardingRepositoryImpl(
    private val dataStore: OnboardingPreferencesDataStore,
) : OnboardingRepository {
    override suspend fun isOnboarded(): Boolean = dataStore.isOnboarded()

    override suspend fun setOnboarded() = dataStore.setOnboarded()
}
