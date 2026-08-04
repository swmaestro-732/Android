package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseDetail.CourseDetailApiService
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.place.PlaceApiService
import com.chillsam.courmy.course.data.place.PlaceDataSource
import com.chillsam.courmy.course.data.place.PlaceRepositoryImpl
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.domain.PlaceRepository
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

    @Provides
    @Singleton
    fun providePlaceApiService(retrofit: Retrofit): PlaceApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlaceDataSource(apiService: PlaceApiService): PlaceDataSource = PlaceDataSource(apiService)

    @Provides
    @Singleton
    fun providePlaceRepository(dataSource: PlaceDataSource): PlaceRepository = PlaceRepositoryImpl(dataSource)
}
