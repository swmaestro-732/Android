package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.domain.saved.SavedCourseRepository
import com.chillsam.courmy.main.domain.saved.SavedPlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SavedDataModule {
    @Provides
    @Singleton
    fun provideSavedCourseApiService(retrofit: Retrofit): SavedCourseApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideSavedCourseDataSource(apiService: SavedCourseApiService): SavedCourseDataSource =
        SavedCourseDataSource(apiService)

    @Provides
    @Singleton
    fun provideSavedCourseRepository(dataSource: SavedCourseDataSource): SavedCourseRepository =
        SavedCourseRepositoryImpl(dataSource)

    @Provides
    @Singleton
    fun provideSavedPlaceApiService(retrofit: Retrofit): SavedPlaceApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideSavedPlaceDataSource(apiService: SavedPlaceApiService): SavedPlaceDataSource =
        SavedPlaceDataSource(apiService)

    @Provides
    @Singleton
    fun provideSavedPlaceRepository(dataSource: SavedPlaceDataSource): SavedPlaceRepository =
        SavedPlaceRepositoryImpl(dataSource)
}
