package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.domain.PlanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 계획(플랜) 전용 DI — 프로필 계획 탭 목록 + 홈 FAB "계획하기" 생성. */
@Module
@InstallIn(SingletonComponent::class)
object PlanDataModule {
    @Provides
    @Singleton
    fun providePlanApiService(retrofit: Retrofit): PlanApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlanDataSource(apiService: PlanApiService): PlanDataSource = PlanDataSource(apiService)

    @Provides
    @Singleton
    fun providePlanRepository(dataSource: PlanDataSource): PlanRepository = PlanRepositoryImpl(dataSource)
}
