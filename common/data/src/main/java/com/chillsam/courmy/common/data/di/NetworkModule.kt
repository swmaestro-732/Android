package com.chillsam.courmy.common.data.di

import com.chillsam.courmy.common.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // API-CONFIG-INJECTION-POINT: base URL / API key 는 local.properties → BuildConfig 로 주입된다.
    // (기본값은 레퍼런스 feature 가 사용하는 카카오 검색 OpenAPI. common/data/build.gradle.kts 참고)

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging =
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        // API-CONFIG-INJECTION-POINT: 인증 헤더는 API_KEY 가 있을 때만 붙인다.
        // Courmy 공개 엔드포인트(코스 상세 등)는 키가 없어도 되고, /my·생성 등 인증 필요 API 는
        // 로그인 accessToken(Bearer) 도입 시 이 포맷을 교체한다.
        val authInterceptor =
            Interceptor { chain ->
                val original = chain.request()
                val request =
                    if (BuildConfig.API_KEY.isNotBlank()) {
                        original
                            .newBuilder()
                            .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                            .build()
                    } else {
                        original
                    }
                chain.proceed(request)
            }
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
    }
}
