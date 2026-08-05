package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.main.domain.home.HomeFeedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeDataModule {
    @Provides
    @Singleton
    fun provideHomeFeedApiService(retrofit: Retrofit): HomeFeedApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideHomeFeedDataSource(apiService: HomeFeedApiService): HomeFeedDataSource = HomeFeedDataSource(apiService)

    @Provides
    @Singleton
    fun provideHomeFeedRepository(dataSource: HomeFeedDataSource): HomeFeedRepository =
        HomeFeedRepositoryImpl(dataSource)
}
