package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

// Figma design_system > BADGE 기준값. 배경은 시맨틱 색의 12% 틴트(신규 팔레트 없이 alpha 파생).
private val BadgeHorizontalPadding = 12.dp
private val BadgeVerticalPadding = 6.dp
private val BadgeContentGap = 5.dp
private val DotSize = 6.dp
private const val BADGE_TINT_ALPHA = 0.12f
private const val PILL_PERCENT = 50

enum class DsBadgeStyle {
    /** 성공·긍정 상태(예: 영업 중). */
    Success,

    /** 강조·중립 상태(예: 미방문). */
    Accent,
}

/**
 * 틴트 배경 배지. Figma `design_system > BADGE`.
 * 텍스트색은 시맨틱 색, 배경은 그 색의 12% 틴트.
 * [showDot]=true 면 텍스트 앞에 같은 색 상태 인디케이터(막대)를 붙인다(예: 영업 중).
 *
 * ★ 평점 표기는 RATING·REVIEW 도메인 컴포넌트에서 별도로 다룬다(여기 미포함).
 */
@Composable
fun DsBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: DsBadgeStyle = DsBadgeStyle.Accent,
    showDot: Boolean = false,
) {
    val color: Color =
        when (style) {
            DsBadgeStyle.Success -> DesignSystemThemeImpl.designSystemColor.contentSuccess
            DsBadgeStyle.Accent -> DesignSystemThemeImpl.designSystemColor.contentAccent
        }

    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = PILL_PERCENT))
                .background(color.copy(alpha = BADGE_TINT_ALPHA))
                .padding(horizontal = BadgeHorizontalPadding, vertical = BadgeVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(BadgeContentGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showDot) {
            Box(
                modifier =
                    Modifier
                        .size(DotSize)
                        .clip(CircleShape)
                        .background(color),
            )
        }
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color,
        )
    }
}

@Preview
@Composable
private fun DsBadgeSuccessPreview() {
    DesignSystemTheme {
        DsBadge(text = "영업 중", style = DsBadgeStyle.Success, showDot = true)
    }
}

@Preview
@Composable
private fun DsBadgeAccentPreview() {
    DesignSystemTheme {
        DsBadge(text = "미방문", style = DsBadgeStyle.Accent)
    }
}
