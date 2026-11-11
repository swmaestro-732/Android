package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.translation.LocalAppLanguage
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 화면이 AI(온디바이스 MT)로 번역 중임을 알리는 작은 배지(번역 아이콘 + "AI").
 *
 * 선택 언어가 한국어가 아닐 때만(= 자동 번역이 켜졌을 때만) 보인다. 화면을 가리지 않게 작고
 * 반투명하게 둔다. 라벨은 언어 중립적인 "AI" 고정이라 재번역되지 않는다.
 */
@Composable
fun AiTranslationBadge(modifier: Modifier = Modifier) {
    if (LocalAppLanguage.current == AppLanguage.KOREAN) return
    val colors = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(100.dp))
                .background(colors.contentDefaultLevel0.copy(alpha = BADGE_ALPHA))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_translate_16),
            contentDescription = null,
            tint = colors.contentOnAccent,
            modifier = Modifier.size(13.dp),
        )
        DsText(
            text = "AI",
            style = DesignSystemThemeImpl.typeScale.textExtraXS,
            color = colors.contentOnAccent,
        )
    }
}

/** 콘텐츠를 가리지 않게 반투명으로 얹는다. */
private const val BADGE_ALPHA = 0.72f
