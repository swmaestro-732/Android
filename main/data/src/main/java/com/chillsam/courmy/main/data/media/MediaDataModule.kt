package com.chillsam.courmy.main.data.media

import android.content.Context
import com.chillsam.courmy.main.domain.media.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaDataModule {
    @Provides
    @Singleton
    fun provideUploadApiService(retrofit: Retrofit): UploadApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideMediaRepository(
        apiService: UploadApiService,
        @ApplicationContext context: Context,
    ): MediaRepository = MediaRepositoryImpl(apiService, context)
}
