package com.chillsam.courmy.main.data.my

import com.chillsam.courmy.main.domain.my.MyProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyDataModule {
    @Provides
    @Singleton
    fun provideMyProfileApiService(retrofit: Retrofit): MyProfileApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideMyProfileDataSource(apiService: MyProfileApiService): MyProfileDataSource =
        MyProfileDataSource(apiService)

    @Provides
    @Singleton
    fun provideMyProfileRepository(dataSource: MyProfileDataSource): MyProfileRepository =
        MyProfileRepositoryImpl(dataSource)
}
