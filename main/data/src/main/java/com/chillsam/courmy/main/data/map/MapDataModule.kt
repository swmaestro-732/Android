package com.chillsam.courmy.main.data.map

import com.chillsam.courmy.main.domain.map.MapPlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapDataModule {
    @Provides
    @Singleton
    fun providePlaceMapApiService(retrofit: Retrofit): PlaceMapApiService = retrofit.create()

    @Provides
    @Singleton
    fun providePlaceMapDataSource(apiService: PlaceMapApiService): PlaceMapDataSource = PlaceMapDataSource(apiService)

    @Provides
    @Singleton
    fun providePlaceMapRepository(dataSource: PlaceMapDataSource): MapPlaceRepository =
        PlaceMapRepositoryImpl(dataSource)
}
