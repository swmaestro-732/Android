package com.chillsam.courmy.common.data.push

import com.chillsam.courmy.common.domain.push.NotificationSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {
    @Provides
    fun notificationSettings(devices: DeviceRegistration): NotificationSettings = devices

    @Provides
    @Singleton
    fun deviceApi(retrofit: Retrofit): DeviceApi = retrofit.create()
}
