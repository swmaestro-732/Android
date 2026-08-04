package com.chillsam.courmy

import android.app.Application
import com.chillsam.courmy.main.presentation.profile.SampleProfileStore
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
        // 더미 모드에서 가입·편집한 프로필을 재시작 후에도 유지하기 위한 복원(실 연동 시 no-op).
        SampleProfileStore.init(this)
    }
}
