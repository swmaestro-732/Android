package com.chillsam.courmy.main.data.aichat

import com.chillsam.courmy.main.domain.aichat.AiChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiChatDataModule {
    @Provides
    @Singleton
    fun provideAiChatApiService(retrofit: Retrofit): AiChatApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideAiChatDataSource(apiService: AiChatApiService): AiChatDataSource = AiChatDataSource(apiService)

    @Provides
    @Singleton
    fun provideAiChatRepository(dataSource: AiChatDataSource): AiChatRepository = AiChatRepositoryImpl(dataSource)
}
