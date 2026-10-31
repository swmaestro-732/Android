package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.course.domain.TraceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 따라가기 기록(trace) 전용 DI — 따라가기 화면의 시작·방문 체크·종료 기록. */
@Module
@InstallIn(SingletonComponent::class)
object TraceDataModule {
    @Provides
    @Singleton
    fun provideTraceApiService(retrofit: Retrofit): TraceApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideTraceDataSource(apiService: TraceApiService): TraceDataSource = TraceDataSource(apiService)

    @Provides
    @Singleton
    fun provideTraceRepository(dataSource: TraceDataSource): TraceRepository = TraceRepositoryImpl(dataSource)
}
