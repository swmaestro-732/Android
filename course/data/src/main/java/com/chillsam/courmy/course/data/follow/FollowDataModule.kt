package com.chillsam.courmy.course.data.follow

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 코스 상세의 작성자 팔로우 전용 DI. */
@Module
@InstallIn(SingletonComponent::class)
object FollowDataModule {
    @Provides
    @Singleton
    fun provideFollowApiService(retrofit: Retrofit): FollowApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideFollowDataSource(apiService: FollowApiService): FollowDataSource = FollowDataSource(apiService)
}
