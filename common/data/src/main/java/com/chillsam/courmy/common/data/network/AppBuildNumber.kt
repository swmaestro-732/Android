package com.chillsam.courmy.common.data.network

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat

/**
 * 설치된 APK 의 versionCode.
 *
 * 라이브러리 모듈 BuildConfig 에 따로 심지 않고 매니페스트에서 읽는다 — 값을 두 군데 두면 릴리스 때
 * `app/build.gradle.kts` 의 `versionCode` 만 올리고 이쪽을 잊는 순간, 서버가 새 앱을 낡은 앱으로
 * 오판해 전원에게 강제 업데이트를 띄운다. 매니페스트는 그 `versionCode` 가 그대로 흘러들어온 곳이라
 * 어긋날 수가 없고, Play 가 실제로 설치한 빌드와도 항상 일치한다.
 *
 * 자기 자신의 패키지를 조회하는 것이라 실패할 일은 사실상 없지만, 실패했을 때 0 같은 기본값으로
 * 떨어지면 그게 곧 "아주 낡은 앱"이 되므로 null 로 남겨 헤더를 생략시킨다([AppVersionInterceptor]).
 */
internal fun Context.appBuildNumber(): Long? =
    runCatching {
        @Suppress("DEPRECATION")
        PackageInfoCompat.getLongVersionCode(packageManager.getPackageInfo(packageName, 0))
    }.getOrNull()
