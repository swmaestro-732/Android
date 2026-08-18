package com.chillsam.courmy.common.data.di

import com.chillsam.courmy.common.data.BuildConfig
import com.chillsam.courmy.common.data.auth.TokenAuthenticator
import com.chillsam.courmy.common.data.auth.TokenReissueApi
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.data.logging.ApiLogInterceptor
import com.chillsam.courmy.common.data.session.SessionEventBusImpl
import com.chillsam.courmy.common.data.telemetry.TelemetryInterceptor
import com.chillsam.courmy.common.domain.session.SessionEventBus
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
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
    fun provideOkHttpClient(
        tokenStore: TokenStore,
        tokenAuthenticator: TokenAuthenticator,
        telemetry: Telemetry,
        json: Json,
    ): OkHttpClient {
        // request/response 를 'API' 태그로 남기는 debug 전용 로깅(토큰 자동 마스킹).
        // 릴리스에서는 enabled=false 라 아무것도 남기지 않아 토큰이 logcat 으로 새지 않는다.
        val apiLogging = ApiLogInterceptor(json, enabled = BuildConfig.DEBUG)
        return OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor(tokenStore))
            .addInterceptor(apiLogging)
            // 릴리스에서도 동작 — 실패만 원격으로 남긴다(본문·헤더 미포함).
            .addInterceptor(TelemetryInterceptor(telemetry))
            // 401 → refreshToken 으로 accessToken 재발급 후 원요청 1회 재시도.
            .authenticator(tokenAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 우리 API 호스트로 가는 요청에만, accessToken 이 있을 때 Authorization 을 붙인다.
    // 호스트를 확인해 서드파티 호스트·CDN·프리사인 업로드 URL 로 토큰이 새지 않게 하고,
    // 토큰이 없으면 헤더를 생략한다(코스 상세 등 공개 엔드포인트).
    private val apiHost: String? = BuildConfig.API_BASE_URL.toHttpUrlOrNull()?.host

    private fun authInterceptor(tokenStore: TokenStore): Interceptor =
        Interceptor { chain ->
            val original = chain.request()
            val token = tokenStore.accessToken
            val request =
                if (token != null && original.url.host == apiHost) {
                    original
                        .newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    original
                }
            chain.proceed(request)
        }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = retrofit(okHttpClient, json)

    /**
     * 토큰 재발급 전용 bare 클라이언트/Retrofit. 인증 인터셉터·[TokenAuthenticator] 를 붙이지 않아
     * 재발급 요청이 다시 401→재발급으로 재귀하지 않는다.
     */
    @Provides
    @Singleton
    @Named(BARE)
    fun provideBareOkHttpClient(
        telemetry: Telemetry,
        json: Json,
    ): OkHttpClient {
        val apiLogging = ApiLogInterceptor(json, enabled = BuildConfig.DEBUG)
        return OkHttpClient
            .Builder()
            .addInterceptor(apiLogging)
            // 재발급 실패는 토큰 만료 시점에 강제 로그아웃으로 이어지므로 운영에서 반드시 보여야 한다.
            .addInterceptor(TelemetryInterceptor(telemetry))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named(BARE)
    fun provideBareRetrofit(
        @Named(BARE) okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = retrofit(okHttpClient, json)

    @Provides
    @Singleton
    fun provideTokenReissueApi(
        @Named(BARE) retrofit: Retrofit,
    ): TokenReissueApi = retrofit.create()

    @Provides
    @Singleton
    fun provideSessionEventBus(impl: SessionEventBusImpl): SessionEventBus = impl

    private fun retrofit(
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

    private const val BARE = "bare"
}
