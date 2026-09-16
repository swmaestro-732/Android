package com.chillsam.courmy.main.data.follow

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.domain.follow.FollowListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FollowDataModule {
    @Provides
    @Singleton
    fun provideFollowListApiService(retrofit: Retrofit): FollowListApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideFollowListDataSource(apiService: FollowListApiService): FollowListDataSource =
        FollowListDataSource(apiService)

    @Provides
    @Singleton
    fun provideFollowListRepository(
        dataSource: FollowListDataSource,
        tokenStore: TokenStore,
    ): FollowListRepository = FollowListRepositoryImpl(dataSource, tokenStore)
}
