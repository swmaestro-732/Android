package com.chillsam.courmy.course.data.courseReviews

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.course.domain.CourseReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 코스 후기(FS-37 전체보기 + 작성·삭제) 전용 DI. */
@Module
@InstallIn(SingletonComponent::class)
object CourseReviewDataModule {
    @Provides
    @Singleton
    fun provideCourseReviewListApiService(retrofit: Retrofit): CourseReviewListApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseReviewWriteApiService(retrofit: Retrofit): CourseReviewWriteApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseReviewListDataSource(
        listApiService: CourseReviewListApiService,
        writeApiService: CourseReviewWriteApiService,
    ): CourseReviewListDataSource = CourseReviewListDataSource(listApiService, writeApiService)

    @Provides
    @Singleton
    fun provideCourseReviewRepository(
        dataSource: CourseReviewListDataSource,
        tokenStore: TokenStore,
    ): CourseReviewRepository = CourseReviewRepositoryImpl(dataSource) { tokenStore.userId }
}
