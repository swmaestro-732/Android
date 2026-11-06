package com.chillsam.courmy.course.data.traces

import com.chillsam.courmy.course.domain.TraceRouteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

/** 도보 경로 조회(`/traces/route`) 전용 DI. 기록(trace)과 달리 세션 없는 단순 조회라 분리한다. */
@Module
@InstallIn(SingletonComponent::class)
object TraceRouteDataModule {
    @Provides
    @Singleton
    fun provideTraceRouteApiService(retrofit: Retrofit): TraceRouteApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideTraceRouteDataSource(apiService: TraceRouteApiService): TraceRouteDataSource =
        TraceRouteDataSource(apiService)

    @Provides
    @Singleton
    fun provideTraceRouteRepository(dataSource: TraceRouteDataSource): TraceRouteRepository =
        TraceRouteRepositoryImpl(dataSource)
}
