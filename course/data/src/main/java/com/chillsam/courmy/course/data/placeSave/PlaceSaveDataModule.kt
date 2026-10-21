package com.chillsam.courmy.course.data.placeSave

import com.chillsam.courmy.course.domain.PlaceSaveRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 장소 저장은 장소 상세 시트·저장함·지도가 함께 쓰는 별도 관심사라 모듈을 나눈다(코스 저장과 대칭). */
@Module
@InstallIn(SingletonComponent::class)
object PlaceSaveDataModule {
    @Provides
    @Singleton
    fun providePlaceSaveApiService(retrofit: Retrofit): PlaceSaveApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlaceSaveDataSource(apiService: PlaceSaveApiService): PlaceSaveDataSource =
        PlaceSaveDataSource(apiService)

    @Provides
    @Singleton
    fun providePlaceSaveRepository(dataSource: PlaceSaveDataSource): PlaceSaveRepository =
        PlaceSaveRepositoryImpl(dataSource)
}
