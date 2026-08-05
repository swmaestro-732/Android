package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.course.domain.CourseManageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 내 코스 관리는 조회·생성과 관심사가 달라 모듈을 나눈다(CourseDataModule 비대화 방지). */
@Module
@InstallIn(SingletonComponent::class)
object CourseManageDataModule {
    @Provides
    @Singleton
    fun provideCourseManageApiService(retrofit: Retrofit): CourseManageApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseManageDataSource(apiService: CourseManageApiService): CourseManageDataSource =
        CourseManageDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseManageRepository(dataSource: CourseManageDataSource): CourseManageRepository =
        CourseManageRepositoryImpl(dataSource)
}
