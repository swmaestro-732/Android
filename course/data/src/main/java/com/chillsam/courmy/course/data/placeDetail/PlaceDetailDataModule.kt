package com.chillsam.courmy.course.data.placeDetail

import com.chillsam.courmy.course.domain.PlaceDetailRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 장소 상세는 코스 생성·검색과 쓰임이 달라 모듈을 나눈다(CourseDataModule 비대화 방지). */
@Module
@InstallIn(SingletonComponent::class)
object PlaceDetailDataModule {
    @Provides
    @Singleton
    fun providePlaceDetailApiService(retrofit: Retrofit): PlaceDetailApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlaceDetailDataSource(apiService: PlaceDetailApiService): PlaceDetailDataSource =
        PlaceDetailDataSource(apiService)

    @Provides
    @Singleton
    fun providePlaceDetailRepository(dataSource: PlaceDetailDataSource): PlaceDetailRepository =
        PlaceDetailRepositoryImpl(dataSource)
}
