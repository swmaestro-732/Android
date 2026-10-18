package com.chillsam.courmy.course.data.courseLike

import com.chillsam.courmy.course.domain.CourseLikeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 코스 좋아요 전용 DI. 저장([com.chillsam.courmy.course.data.courseSave])과 같은 이유로 모듈을 나눈다. */
@Module
@InstallIn(SingletonComponent::class)
object CourseLikeDataModule {
    @Provides
    @Singleton
    fun provideCourseLikeApiService(retrofit: Retrofit): CourseLikeApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseLikeDataSource(apiService: CourseLikeApiService): CourseLikeDataSource =
        CourseLikeDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseLikeRepository(dataSource: CourseLikeDataSource): CourseLikeRepository =
        CourseLikeRepositoryImpl(dataSource)
}
