package com.chillsam.courmy.course.data.recommendedTag

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 추천 태그(`GET /api/v1/recommended-tags`) 전용 DI. */
@Module
@InstallIn(SingletonComponent::class)
object RecommendedTagDataModule {
    @Provides
    @Singleton
    fun provideRecommendedTagApiService(retrofit: Retrofit): RecommendedTagApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideRecommendedTagDataSource(apiService: RecommendedTagApiService): RecommendedTagDataSource =
        RecommendedTagDataSource(apiService)
}
