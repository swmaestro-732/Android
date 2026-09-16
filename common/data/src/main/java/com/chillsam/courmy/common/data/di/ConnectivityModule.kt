package com.chillsam.courmy.common.data.di

import com.chillsam.courmy.common.data.network.ConnectivityRepositoryImpl
import com.chillsam.courmy.common.domain.network.ConnectivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    @Binds
    @Singleton
    abstract fun bindConnectivityRepository(impl: ConnectivityRepositoryImpl): ConnectivityRepository
}
