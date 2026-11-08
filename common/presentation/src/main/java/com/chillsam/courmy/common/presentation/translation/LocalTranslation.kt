package com.chillsam.courmy.common.presentation.translation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.domain.translation.Translator

/**
 * 현재 선택 언어. 앱 루트([MainActivity])에서 저장소를 구독해 제공한다.
 * 언어가 바뀌면 이 값을 읽는 곳만 리컴포즈된다(그래서 static 이 아닌 일반 local).
 */
val LocalAppLanguage = compositionLocalOf { AppLanguage.KOREAN }

/**
 * 온디바이스 번역기. 루트에서 Hilt 로 주입해 제공한다. 제공되지 않으면 번역 UI 를 숨긴다(null).
 * 싱글톤이라 바뀌지 않으므로 static 으로 둔다.
 */
val LocalTranslator = staticCompositionLocalOf<Translator?> { null }

/** 언어의 자기 이름(설정 목록 표시용). */
fun AppLanguage.nativeName(): String =
    when (this) {
        AppLanguage.KOREAN -> "한국어"
        AppLanguage.ENGLISH -> "English"
        AppLanguage.JAPANESE -> "日本語"
    }

/** 번역 토글 문구용 한국어 이름(예: "영어로 번역 보기"). */
fun AppLanguage.koreanName(): String =
    when (this) {
        AppLanguage.KOREAN -> "한국어"
        AppLanguage.ENGLISH -> "영어"
        AppLanguage.JAPANESE -> "일본어"
    }
