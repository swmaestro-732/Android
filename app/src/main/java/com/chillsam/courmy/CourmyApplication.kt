package com.chillsam.courmy

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CourmyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 카카오 로그인 SDK 초기화(네이티브 앱키는 local.properties → BuildConfig 로 주입).
        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()) {
            KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }
    }
}
