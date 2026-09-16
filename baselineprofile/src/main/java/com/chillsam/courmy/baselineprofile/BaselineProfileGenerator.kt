package com.chillsam.courmy.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 앱의 대표적인 사용자 시나리오를 실제 단말에서 실행하면서 ART 가
 * "자주 호출된" 클래스/메서드를 dump 하도록 한다.
 *
 * 레퍼런스 feature 제거 후에는 콜드 스타트(앱 실행 → 기본 홈 화면 컴포지션)만
 * 기록한다. 새 화면을 추가하면 여기에 해당 사용자 여정을 확장한다.
 *
 * 실행 방법
 *   - 연결된 기기:  ./gradlew :app:generateBaselineProfile
 *   - GMD 사용:     ./gradlew :app:generateBaselineProfile -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader
 *
 * 산출물 위치
 *   app/src/release/generated/baselineProfiles/baseline-prof.txt
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() =
        rule.collect(
            packageName = PACKAGE_NAME,
            includeInStartupProfile = true,
        ) {
            // 콜드 스타트 → 기본 홈 화면이 컴포지션될 때까지 대기.
            startActivityAndWait()
            device.waitForIdle()
        }

    companion object {
        private const val PACKAGE_NAME = "com.chillsam.courmy"
    }
}
