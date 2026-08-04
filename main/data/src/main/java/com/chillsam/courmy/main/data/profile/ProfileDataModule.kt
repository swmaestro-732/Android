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
import javax.inject.Named
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

    @Provides
    @Singleton
    fun provideImageUploadApiService(retrofit: Retrofit): ImageUploadApiService = retrofit.create()

    /**
     * 프리사인 URL 로 가는 PUT 은 인증 헤더가 붙지 않는 bare 클라이언트를 쓴다
     * (S3 로 토큰이 새지 않게 + 401 재발급 재시도에 걸리지 않게).
     */
    @Provides
    @Singleton
    fun provideS3UploadApiService(
        @Named(BARE) retrofit: Retrofit,
    ): S3UploadApiService = retrofit.create()

    @Provides
    @Singleton
    fun provideImageUploadDataSource(
        @ApplicationContext context: Context,
        apiService: ImageUploadApiService,
        s3ApiService: S3UploadApiService,
    ): ImageUploadDataSource = ImageUploadDataSource(context, apiService, s3ApiService)

    @Provides
    @Singleton
    fun provideProfileRepository(
        dataSource: ProfileDataSource,
        imageUploadDataSource: ImageUploadDataSource,
        tokenStore: TokenStore,
    ): ProfileRepository = ProfileRepositoryImpl(dataSource, imageUploadDataSource, tokenStore)

    /** common:data 의 [com.chillsam.courmy.common.data.di.NetworkModule] 이 쓰는 qualifier 와 같아야 한다. */
    private const val BARE = "bare"
}
