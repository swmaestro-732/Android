package com.chillsam.courmy.common.presentation.translation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.chillsam.courmy.common.domain.translation.AppLanguage

private val HANGUL_SYLLABLES = '가'..'힣'
private val HANGUL_JAMO = '㄰'..'㆏'

/**
 * 자동 번역 대상인지 판단한다. 한글이 하나라도 있어야 하고(숫자·영문·핸들 등 제외),
 * 해시태그(첫 글자 '#')는 원문을 유지한다(번역이 어울리지 않음).
 */
fun String.isTranslatableText(): Boolean {
    if (isBlank()) return false
    if (trimStart().startsWith("#")) return false
    return any { it in HANGUL_SYLLABLES || it in HANGUL_JAMO }
}

/**
 * [text] 를 현재 선택 언어로 자동 번역한 문자열을 돌려준다(공통 [DsText] 가 사용).
 *
 * - 선택 언어가 한국어거나 번역기가 없거나 번역 대상이 아니면 원문을 그대로 반환한다.
 * - 캐시에 있으면 즉시, 없으면 원문을 먼저 보여 주고 번역이 끝나면 교체한다.
 * - Compose 호출 구조를 일정하게 유지하려고 remember·LaunchedEffect 는 항상 호출하고 내부에서 분기한다.
 *
 * [enabled] 가 false 면 번역하지 않고 원문을 그대로 쓴다. **앱 고정 UI 문자열의 오번역을 막기 위한
 * 기본값**이다 — 고정 문구는 strings.xml 리소스(locale)로 번역하고, 서버 게시글 등 동적 콘텐츠만
 * 호출부에서 enabled=true 로 켠다(MT opt-in).
 */
@Composable
fun rememberAutoTranslated(
    text: String,
    enabled: Boolean,
): String {
    val language = LocalAppLanguage.current
    val translator = LocalTranslator.current
    val translatable = enabled && language != AppLanguage.KOREAN && translator != null && text.isTranslatableText()

    var result by remember(text, language) {
        mutableStateOf(if (translatable) TranslationCache.get(text, language) ?: text else text)
    }

    LaunchedEffect(text, language, translatable) {
        if (!translatable || translator == null) {
            result = text
            return@LaunchedEffect
        }
        val cached = TranslationCache.get(text, language)
        if (cached != null) {
            result = cached
            return@LaunchedEffect
        }
        runCatching { translator.translate(text, language) }
            .onSuccess { translated ->
                TranslationCache.put(text, language, translated)
                result = translated
            }
    }

    return result
}
