package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.main.domain.search.CourseSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchDataModule {
    @Provides
    @Singleton
    fun provideCourseSearchApiService(retrofit: Retrofit): CourseSearchApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseSearchDataSource(apiService: CourseSearchApiService): CourseSearchDataSource =
        CourseSearchDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseSearchRepository(dataSource: CourseSearchDataSource): CourseSearchRepository =
        CourseSearchRepositoryImpl(dataSource)
}
