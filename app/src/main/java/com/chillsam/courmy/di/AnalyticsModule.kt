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
 *
 * debug 빌드도 no-op 이다. Firebase 는 매니페스트(`app/src/debug/AndroidManifest.xml`)로 수집을
 * 끄지만 Amplitude 는 그런 스위치가 없어 여기서 막는다 — 개발 중 이벤트가 운영 지표에 섞이면
 * 전환·세션 집계가 왜곡된다. 로컬에서 전송 자체를 확인하고 싶으면 아래 가드를 잠시 지운다.
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
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "debug 빌드 — Amplitude 비활성 (Firebase 와 동일하게 테스트 이벤트를 운영 지표에서 분리)")
            return NoOpAmplitudeClient
        }
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
        // 앱 실행을 알리는 로드 타임 이벤트(세션 시작 확인용).
        client.track(eventName = "app_opened")
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
