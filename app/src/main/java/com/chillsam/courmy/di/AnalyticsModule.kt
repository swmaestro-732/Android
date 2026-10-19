package com.chillsam.courmy.di

import android.content.Context
import android.util.Log
import com.amplitude.android.Amplitude
import com.amplitude.android.AutocaptureOption
import com.amplitude.android.Configuration
import com.chillsam.courmy.BuildConfig
import com.chillsam.courmy.common.data.telemetry.AmplitudeClient
import com.chillsam.courmy.common.data.telemetry.NoOpAmplitudeClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Amplitude SDK 를 [AmplitudeClient] 로 감싸 common:data 에 제공한다.
 *
 * SDK 의존성을 app 에만 두기 위한 경계다. API 키가 비면(local.properties·CI secret 미설정)
 * 분석만 꺼진 no-op 으로 폴백한다 — 분석은 없어도 앱은 돌아야 한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    private const val TAG = "Analytics"

    @Provides
    @Singleton
    fun provideAmplitudeClient(
        @ApplicationContext context: Context,
    ): AmplitudeClient {
        val apiKey = BuildConfig.AMPLITUDE_API_KEY
        if (apiKey.isBlank()) {
            // 조용히 꺼지면 "왜 이벤트가 안 오지"를 한참 찾게 되므로 한 줄은 남긴다.
            Log.w(TAG, "AMPLITUDE_API_KEY 미설정 — 분석 비활성")
            return NoOpAmplitudeClient
        }
        val amplitude =
            Amplitude(
                Configuration(
                    apiKey = apiKey,
                    context = context,
                    // 세션 구간·앱 시작/종료는 직접 계측하지 않으므로 SDK 자동 수집에 맡긴다.
                    autocapture = setOf(AutocaptureOption.SESSIONS, AutocaptureOption.APP_LIFECYCLES),
                ),
            )
        val client = SdkAmplitudeClient(amplitude)
        // 설치 직후 대시보드에서 수신을 확인하기 위한 로드 타임 이벤트(1회).
        client.track(
            eventName = "app_opened",
            // 설치 검증용 속성 — 대시보드에서 수신 확인 후 제거해도 된다
            properties = mapOf("prompt_version" to "BA400.4"),
        )
        return client
    }

    private class SdkAmplitudeClient(
        private val amplitude: Amplitude,
    ) : AmplitudeClient {
        override fun track(
            eventName: String,
            properties: Map<String, String>,
        ) {
            amplitude.track(eventName, properties)
        }
    }
}
