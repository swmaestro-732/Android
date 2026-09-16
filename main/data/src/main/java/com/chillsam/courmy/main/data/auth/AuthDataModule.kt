package com.chillsam.courmy.main.data.auth

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.domain.auth.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthDataModule {
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideAuthDataSource(apiService: AuthApiService): AuthDataSource = AuthDataSource(apiService)

    @Provides
    @Singleton
    fun provideAuthRepository(
        dataSource: AuthDataSource,
        tokenStore: TokenStore,
    ): AuthRepository = AuthRepositoryImpl(dataSource, tokenStore)
}
