package com.chillsam.courmy.main.data.notification

import com.chillsam.courmy.main.domain.notification.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationDataModule {
    @Provides
    @Singleton
    fun api(retrofit: Retrofit): NotificationApiService = retrofit.create()

    @Provides
    @Singleton
    fun source(api: NotificationApiService): NotificationDataSource = NotificationDataSource(api)

    @Provides
    @Singleton
    fun repository(source: NotificationDataSource): NotificationRepository = NotificationRepositoryImpl(source)
}
