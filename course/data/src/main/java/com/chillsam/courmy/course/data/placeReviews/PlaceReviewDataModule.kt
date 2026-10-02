package com.chillsam.courmy.course.data.placeReviews

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.course.domain.PlaceReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 장소 후기(FS-11 장소 상세 시트) 전용 DI. */
@Module
@InstallIn(SingletonComponent::class)
object PlaceReviewDataModule {
    @Provides
    @Singleton
    fun providePlaceReviewListApiService(retrofit: Retrofit): PlaceReviewListApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlaceReviewListDataSource(apiService: PlaceReviewListApiService): PlaceReviewListDataSource =
        PlaceReviewListDataSource(apiService)

    @Provides
    @Singleton
    fun providePlaceReviewRepository(
        dataSource: PlaceReviewListDataSource,
        tokenStore: TokenStore,
    ): PlaceReviewRepository = PlaceReviewRepositoryImpl(dataSource) { tokenStore.userId }
}
