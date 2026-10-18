package com.chillsam.courmy

import android.app.Application
import com.chillsam.courmy.main.presentation.login.GoogleLoginClient
import com.chillsam.courmy.main.presentation.login.NaverLoginClient
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CourmyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 소셜 로그인 SDK 초기화(키는 local.properties → BuildConfig 로 주입).
        // 키가 비면 초기화를 건너뛰고, 해당 로그인 버튼은 "준비되지 않았어요" 안내를 띄운다.
        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()) {
            KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
        if (BuildConfig.NAVER_LOGIN_CLIENT_ID.isNotBlank() && BuildConfig.NAVER_LOGIN_CLIENT_SECRET.isNotBlank()) {
            NaverIdLoginSDK.initialize(
                this,
                BuildConfig.NAVER_LOGIN_CLIENT_ID,
                BuildConfig.NAVER_LOGIN_CLIENT_SECRET,
                "Courmy",
            )
            NaverLoginClient.isConfigured = true
        }
        GoogleLoginClient.serverClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID
    }
}
