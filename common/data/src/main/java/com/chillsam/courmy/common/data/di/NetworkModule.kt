package com.chillsam.courmy.common.data.di

import com.chillsam.courmy.common.data.BuildConfig
import com.chillsam.courmy.common.data.auth.TokenStore
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
    fun provideOkHttpClient(tokenStore: TokenStore): OkHttpClient {
        val logging =
            HttpLoggingInterceptor().apply {
                // 릴리스에서는 토큰(idToken·access·refresh)이 logcat 으로 새지 않도록 로깅을 끈다.
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }
        // 인증 헤더: 로그인 accessToken 이 있으면 우선 사용하고, 없으면 API_KEY(공개 API·검색용) 폴백.
        // 토큰이 둘 다 없으면 헤더를 붙이지 않는다(코스 상세 등 공개 엔드포인트).
        val authInterceptor =
            Interceptor { chain ->
                val original = chain.request()
                val token = tokenStore.accessToken ?: BuildConfig.API_KEY.ifBlank { null }
                val request =
                    if (token != null) {
                        original
                            .newBuilder()
                            .addHeader("Authorization", "Bearer $token")
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
