package com.chillsam.courmy.main.data.area

import com.chillsam.courmy.main.domain.area.AreaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AreaDataModule {
    @Provides
    @Singleton
    fun provideAreaApiService(retrofit: Retrofit): AreaApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideAreaDataSource(apiService: AreaApiService): AreaDataSource = AreaDataSource(apiService)

    @Provides
    @Singleton
    fun provideAreaRepository(dataSource: AreaDataSource): AreaRepository = AreaRepositoryImpl(dataSource)
}
