package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

// Figma design_system > FILTER CHIP 기준값. 근사색(#f0f0ee)은 bgDefaultLevel0 으로 스냅.
private val ChipHorizontalPadding = 15.dp
private val ChipVerticalPadding = 8.dp
private val ChipBorderWidth = 1.dp
private const val DISABLED_ALPHA = 0.4f
private const val PILL_PERCENT = 50

/**
 * 필터 칩. Figma `design_system > FILTER CHIP` 의 3상태.
 * - Selected: 강조 배경([DesignSystemSemanticColors.bgAccent]) + on-accent 텍스트
 * - Default: 옅은 배경 + 기본 텍스트
 * - Disabled([enabled]=false): 전체 opacity 40%, 클릭 비활성
 */
@Composable
fun DsChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val backgroundColor =
        if (selected) {
            DesignSystemThemeImpl.designSystemColor.bgAccent
        } else {
            DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1
        }
    val contentColor =
        if (selected) {
            DesignSystemThemeImpl.designSystemColor.contentOnAccent
        } else {
            DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1
        }

    val shape = RoundedCornerShape(percent = PILL_PERCENT)

    Box(
        modifier =
            modifier
                .alpha(if (enabled) 1f else DISABLED_ALPHA)
                .clip(shape)
                .background(backgroundColor)
                // 미선택 칩: 흰 배경 + 테두리(Figma #e2e2dd ≈ borderDefaultLevel0). 선택 칩은 채운 강조라 테두리 없음.
                .then(
                    if (!selected) {
                        Modifier.border(
                            width = ChipBorderWidth,
                            color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
                            shape = shape,
                        )
                    } else {
                        Modifier
                    },
                ).clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = ChipHorizontalPadding, vertical = ChipVerticalPadding),
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun DsChipDefaultPreview() {
    DesignSystemTheme {
        DsChip(text = "카페", selected = false, onClick = {})
    }
}

@Preview
@Composable
private fun DsChipSelectedPreview() {
    DesignSystemTheme {
        DsChip(text = "카페", selected = true, onClick = {})
    }
}

@Preview
@Composable
private fun DsChipDisabledPreview() {
    DesignSystemTheme {
        DsChip(text = "카페", selected = false, enabled = false, onClick = {})
    }
}
