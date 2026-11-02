package com.chillsam.courmy.main.data.profile

import android.content.Context
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.main.data.area.LegalAreaCodeMapper
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

    /** 기존 가입·저장 호환 캐시. 표시 값은 계정 프로필 GET 응답을 사용한다. */
    @Provides
    @Singleton
    fun provideInterestDataStore(
        @ApplicationContext context: Context,
    ): InterestPreferencesDataStore = InterestPreferencesDataStore(context)

    @Provides
    @Singleton
    fun provideProfileRepository(
        dataSource: ProfileDataSource,
        tokenStore: TokenStore,
        areaCodeMapper: LegalAreaCodeMapper,
        interestStore: InterestPreferencesDataStore,
    ): ProfileRepository = ProfileRepositoryImpl(dataSource, tokenStore, areaCodeMapper, interestStore)
}
