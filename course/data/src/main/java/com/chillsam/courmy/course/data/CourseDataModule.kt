package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.domain.CourseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CourseDataModule {
    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseRemoteDataSource(apiService: CourseApiService): CourseRemoteDataSource =
        CourseRemoteDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseRepository(remoteDataSource: CourseRemoteDataSource): CourseRepository =
        CourseRepositoryImpl(remoteDataSource)
}
