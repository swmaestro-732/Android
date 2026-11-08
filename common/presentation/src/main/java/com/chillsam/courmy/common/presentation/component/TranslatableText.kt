package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.translation.LocalAppLanguage
import com.chillsam.courmy.common.presentation.translation.LocalTranslator
import com.chillsam.courmy.common.presentation.translation.koreanName
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 한국어 본문에 "번역 보기" 토글을 붙인 텍스트. 원문을 항상 보존한다.
 *
 * - 선택 언어가 한국어([LocalAppLanguage])거나 번역기([LocalTranslator])가 없으면 평범한 [DsText] 로 동작.
 * - 그 외엔 본문 아래 토글을 두고, 누르면 선택 언어로 번역해 보여 준다. 다시 누르면 원문으로 되돌린다.
 * - 번역 결과는 (본문·언어) 기준으로 캐시하고, 실패 시 다시 시도할 수 있다.
 *
 * 해시태그·고유명사처럼 번역이 어울리지 않는 텍스트에는 쓰지 않는다(원문 [DsText] 유지).
 */
@Composable
fun TranslatableText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
    maxLines: Int = Int.MAX_VALUE,
) {
    val language = LocalAppLanguage.current
    val translator = LocalTranslator.current
    if (language == AppLanguage.KOREAN || translator == null || text.isBlank()) {
        DsText(text = text, modifier = modifier, color = color, style = style, maxLines = maxLines)
        return
    }

    var showTranslation by remember(text, language) { mutableStateOf(false) }
    var translated by remember(text, language) { mutableStateOf<String?>(null) }
    var loading by remember(text, language) { mutableStateOf(false) }
    var failed by remember(text, language) { mutableStateOf(false) }
    // 실패 후 재시도 트리거. 올려서 effect 를 다시 돌린다.
    var attempt by remember(text, language) { mutableIntStateOf(0) }

    LaunchedEffect(showTranslation, attempt, text, language) {
        if (showTranslation && translated == null) {
            loading = true
            failed = false
            runCatching { translator.translate(text, language) }
                .onSuccess { translated = it }
                .onFailure { failed = true }
            loading = false
        }
    }

    Column(modifier = modifier) {
        DsText(
            text = if (showTranslation && translated != null) translated!! else text,
            color = color,
            style = style,
            maxLines = maxLines,
        )
        Spacer(Modifier.height(6.dp))
        val accent = DesignSystemThemeImpl.designSystemColor.contentAccent
        val muted = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3
        val toggleStyle = DesignSystemThemeImpl.typeScale.textRegularXS
        when {
            loading -> {
                DsText(text = "번역 중…", color = muted, style = toggleStyle)
            }

            failed -> {
                DsText(
                    text = "번역하지 못했어요 · 다시 시도",
                    color = accent,
                    style = toggleStyle,
                    modifier = Modifier.clickable { attempt++ },
                )
            }

            showTranslation && translated != null -> {
                DsText(
                    text = "원문 보기",
                    color = accent,
                    style = toggleStyle,
                    modifier = Modifier.clickable { showTranslation = false },
                )
            }

            else -> {
                DsText(
                    text = "${language.koreanName()}로 번역 보기",
                    color = accent,
                    style = toggleStyle,
                    modifier = Modifier.clickable { showTranslation = true },
                )
            }
        }
    }
}
