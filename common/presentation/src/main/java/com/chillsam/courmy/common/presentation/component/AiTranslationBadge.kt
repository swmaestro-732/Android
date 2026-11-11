package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.translation.LocalAppLanguage
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 화면이 AI(온디바이스 MT)로 번역 중임을 알리는 배지.
 *
 * 선택 언어가 한국어가 아닐 때만(= 자동 번역이 켜졌을 때만) 보인다. 문구는 선택 언어로 고정해
 * 그 자체가 다시 번역되지 않게 한다(한글이 없어 MT 대상에서 제외되기도 한다).
 */
@Composable
fun AiTranslationBadge(modifier: Modifier = Modifier) {
    val label =
        when (LocalAppLanguage.current) {
            AppLanguage.KOREAN -> return
            AppLanguage.ENGLISH -> "AI translated"
            AppLanguage.JAPANESE -> "AI翻訳"
        }
    val colors = DesignSystemThemeImpl.designSystemColor
    DsText(
        text = label,
        style = DesignSystemThemeImpl.typeScale.textExtraXS,
        color = colors.contentOnAccent,
        modifier =
            modifier
                .clip(RoundedCornerShape(100.dp))
                .background(colors.contentDefaultLevel0.copy(alpha = BADGE_ALPHA))
                .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

/** 콘텐츠를 가리지 않게 반투명으로 얹는다. */
private const val BADGE_ALPHA = 0.72f
