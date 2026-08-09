package com.chillsam.courmy.common.data.di

import com.chillsam.courmy.common.data.auth.SessionRepositoryImpl
import com.chillsam.courmy.common.domain.auth.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}
