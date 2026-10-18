package com.chillsam.courmy.course.data.courseComments

import com.chillsam.courmy.course.domain.CourseCommentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 코스 댓글 전용 DI — 댓글 화면의 목록·작성·수정·삭제. */
@Module
@InstallIn(SingletonComponent::class)
object CourseCommentDataModule {
    @Provides
    @Singleton
    fun provideCourseCommentApiService(retrofit: Retrofit): CourseCommentApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseCommentDataSource(apiService: CourseCommentApiService): CourseCommentDataSource =
        CourseCommentDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseCommentRepository(dataSource: CourseCommentDataSource): CourseCommentRepository =
        CourseCommentRepositoryImpl(dataSource)
}
