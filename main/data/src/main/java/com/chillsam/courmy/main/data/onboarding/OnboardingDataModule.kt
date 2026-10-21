package com.chillsam.courmy.main.data.onboarding

import android.content.Context
import com.chillsam.courmy.main.domain.onboarding.OnboardingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingDataModule {
    @Provides
    @Singleton
    fun provideOnboardingDataStore(
        @ApplicationContext context: Context,
    ): OnboardingPreferencesDataStore = OnboardingPreferencesDataStore(context)

    @Provides
    @Singleton
    fun provideOnboardingRepository(dataStore: OnboardingPreferencesDataStore): OnboardingRepository =
        OnboardingRepositoryImpl(dataStore)
}
