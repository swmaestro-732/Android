package com.chillsam.courmy

import android.app.Application
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.main.presentation.login.GoogleLoginClient
import com.chillsam.courmy.main.presentation.login.NaverLoginClient
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CourmyApplication : Application() {
    @Inject
    lateinit var telemetry: Telemetry

    override fun onCreate() {
        super.onCreate()
        // 소셜 로그인 SDK 초기화(키는 local.properties → BuildConfig 로 주입).
        // 키가 비면 초기화를 건너뛰고, 해당 로그인 버튼은 "준비되지 않았어요" 안내를 띄운다.
        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()) {
            KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
        if (BuildConfig.NAVER_LOGIN_CLIENT_ID.isNotBlank() && BuildConfig.NAVER_LOGIN_CLIENT_SECRET.isNotBlank()) {
            // Keystore 마스터 키가 깨진 기기에서 initialize 가 크래시하는 문제의 방어선(SCRUM-627).
            NaverLoginClient.isConfigured =
                initializeNaverSdkOrDisable(
                    clientId = BuildConfig.NAVER_LOGIN_CLIENT_ID,
                    clientSecret = BuildConfig.NAVER_LOGIN_CLIENT_SECRET,
                    clientName = "Courmy",
                    telemetry = telemetry,
                )
        }
        GoogleLoginClient.serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID
    }
}
