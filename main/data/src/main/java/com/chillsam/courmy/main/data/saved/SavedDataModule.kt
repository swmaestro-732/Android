package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.domain.saved.SavedCourseRepository
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
}
