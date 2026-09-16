package com.chillsam.courmy.common.data.di

import android.content.Context
import com.chillsam.courmy.common.data.appUpdate.AppUpdateEventBusImpl
import com.chillsam.courmy.common.data.network.AppVersionInterceptor
import com.chillsam.courmy.common.data.network.apiHost
import com.chillsam.courmy.common.data.network.appBuildNumber
import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 앱 강제 업데이트(HTTP 426) 관련 바인딩.
 *
 * [NetworkModule] 에서 떼어 둔다 — 여기 있는 것들은 OkHttp 조립이 아니라 "서버가 정한 최소 빌드"
 * 라는 하나의 정책에 묶여 있고, 네트워크 모듈에 계속 얹으면 성격이 다른 provider 가 쌓인다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppUpdateModule {
    @Provides
    @Singleton
    fun provideAppUpdateEventBus(impl: AppUpdateEventBusImpl): AppUpdateEventBus = impl

    /**
     * 빌드 번호 조회는 PackageManager 왕복이라 두 OkHttp 클라이언트가 각자 읽지 않도록 한 번만 만든다.
     */
    @Provides
    @Singleton
    fun provideAppVersionInterceptor(
        @ApplicationContext context: Context,
    ): AppVersionInterceptor = AppVersionInterceptor(apiHost, context.appBuildNumber())
}
