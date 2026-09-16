package com.chillsam.courmy.course.data.directions

import com.chillsam.courmy.course.domain.DirectionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DirectionsDataModule {
    @Provides
    @Singleton
    fun provideDirectionsApiService(retrofit: Retrofit): DirectionsApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideDirectionsDataSource(apiService: DirectionsApiService): DirectionsDataSource =
        DirectionsDataSource(apiService)

    @Provides
    @Singleton
    fun provideDirectionsRepository(dataSource: DirectionsDataSource): DirectionsRepository =
        DirectionsRepositoryImpl(dataSource)
}
