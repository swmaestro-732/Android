package com.chillsam.courmy.course.data.courseFolders

import com.chillsam.courmy.course.domain.CourseFolderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 저장 폴더 전용 DI. 저장함 폴더 칩·폴더 지정 저장 UI 가 생기면 그대로 주입해 쓴다. */
@Module
@InstallIn(SingletonComponent::class)
object CourseFolderDataModule {
    @Provides
    @Singleton
    fun provideCourseFolderApiService(retrofit: Retrofit): CourseFolderApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideCourseFolderDataSource(apiService: CourseFolderApiService): CourseFolderDataSource =
        CourseFolderDataSource(apiService)

    @Provides
    @Singleton
    fun provideCourseFolderRepository(dataSource: CourseFolderDataSource): CourseFolderRepository =
        CourseFolderRepositoryImpl(dataSource)
}
