package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.domain.CourseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CourseDataModule {
    @Provides
    @Singleton
    fun provideCourseRepository(): CourseRepository = CourseRepositoryImpl()
}
