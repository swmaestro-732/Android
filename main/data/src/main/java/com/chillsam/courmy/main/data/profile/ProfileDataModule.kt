package com.chillsam.courmy.main.data.profile

import android.content.Context
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.domain.profile.ProfileRepository
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
object ProfileDataModule {
    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideProfileDataSource(apiService: ProfileApiService): ProfileDataSource = ProfileDataSource(apiService)

    /** TODO-API-SPEC: 서버에 bio 필드가 생기면 이 provider 를 제거한다. [wiki-needed] */
    @Provides
    @Singleton
    fun provideBioDataStore(
        @ApplicationContext context: Context,
    ): BioPreferencesDataStore = BioPreferencesDataStore(context)

    @Provides
    @Singleton
    fun provideProfileRepository(
        dataSource: ProfileDataSource,
        tokenStore: TokenStore,
        bioStore: BioPreferencesDataStore,
    ): ProfileRepository = ProfileRepositoryImpl(dataSource, tokenStore, bioStore)
}
