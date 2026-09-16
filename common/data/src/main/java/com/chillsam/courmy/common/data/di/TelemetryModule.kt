package com.chillsam.courmy.common.data.di

import com.chillsam.courmy.common.data.telemetry.CrashlyticsTelemetry
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TelemetryModule {
    @Binds
    @Singleton
    abstract fun bindTelemetry(impl: CrashlyticsTelemetry): Telemetry
}
