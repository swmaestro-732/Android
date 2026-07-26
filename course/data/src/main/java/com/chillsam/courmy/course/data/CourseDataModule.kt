package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseDetail.CourseDetailApiService
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
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
    fun provideCourseDetailApiService(retrofit: Retrofit): CourseDetailApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseDetailDataSource(apiService: CourseDetailApiService): CourseDetailDataSource =
        CourseDetailDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseRepository(courseDetailDataSource: CourseDetailDataSource): CourseRepository =
        CourseRepositoryImpl(courseDetailDataSource)
}
