package com.chillsam.courmy.course.data.courseSave

import com.chillsam.courmy.course.domain.CourseSaveRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 코스 저장은 상세·피드·저장함이 함께 쓰는 별도 관심사라 모듈을 나눈다. */
@Module
@InstallIn(SingletonComponent::class)
object CourseSaveDataModule {
    @Provides
    @Singleton
    fun provideCourseSaveApiService(retrofit: Retrofit): CourseSaveApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseSaveDataSource(apiService: CourseSaveApiService): CourseSaveDataSource =
        CourseSaveDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseSaveRepository(dataSource: CourseSaveDataSource): CourseSaveRepository =
        CourseSaveRepositoryImpl(dataSource)
}
