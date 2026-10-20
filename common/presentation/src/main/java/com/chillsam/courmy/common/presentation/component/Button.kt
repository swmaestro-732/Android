package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

// Figma design_system > BUTTONS / PRIMARY BUTTON 기준값. 팔레트/타이포는 토큰만 사용(raw hex/sp 금지).
private val ButtonHeight = 52.dp
private val ButtonCornerRadius = 16.dp
private val ButtonSpinnerSize = 18.dp
private const val DISABLED_ALPHA = 0.4f

enum class DsButtonVariant {
    /** 채워진 강조 CTA (Forest600). */
    Primary,

    /** 흰 배경 + 테두리 + 강조 텍스트. 보조 액션용(기존 ForestTint 배경에서 변경, 2026-10-19). */
    Secondary,
}

/**
 * 공용 버튼. Figma `design_system > BUTTONS / PRIMARY BUTTON` 기준.
 * 상태: Default / Pressed / Loading / Disabled(전체 opacity 40%).
 * Pressed 색은 [DsButtonVariant.Primary] 에만 별도 토큰([DesignSystemSemanticColors.bgAccentPressed])이 있다.
 *
 * Kakao 로그인 버튼은 브랜드 색이라 로그인 화면 작업 시 별도 추가한다.
 */
@Composable
fun DsButton(
    text: String,
    modifier: Modifier = Modifier,
    variant: DsButtonVariant = DsButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor: Color =
        when (variant) {
            DsButtonVariant.Primary -> {
                if (isPressed) {
                    DesignSystemThemeImpl.designSystemColor.bgAccentPressed
                } else {
                    DesignSystemThemeImpl.designSystemColor.bgAccent
                }
            }

            DsButtonVariant.Secondary -> {
                // 흰 면 토큰. bgDefaultLevel0(화면 바탕)과 구분되는 카드·버튼용 흰색이다.
                DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1
            }
        }
    val contentColor: Color =
        when (variant) {
            DsButtonVariant.Primary -> DesignSystemThemeImpl.designSystemColor.contentOnAccent
            DsButtonVariant.Secondary -> DesignSystemThemeImpl.designSystemColor.contentAccent
        }
    // 흰 배경은 흰 화면 위에서 면이 사라져 테두리로 경계를 만든다(Secondary 전용).
    val borderColor: Color? =
        when (variant) {
            DsButtonVariant.Primary -> null
            DsButtonVariant.Secondary -> DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = ButtonHeight)
                .alpha(if (enabled) 1f else DISABLED_ALPHA)
                .clip(RoundedCornerShape(ButtonCornerRadius))
                .background(backgroundColor)
                .then(
                    borderColor?.let {
                        Modifier.border(1.dp, it, RoundedCornerShape(ButtonCornerRadius))
                    } ?: Modifier,
                ).clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled && !loading,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonSpinnerSize),
                color = contentColor,
            )
        } else {
            DsText(
                text = text,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = contentColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun DsButtonPreview() {
    DesignSystemTheme {
        DsButton(text = "따라가기", onClick = {})
    }
}

@Preview
@Composable
private fun DsButtonSecondaryPreview() {
    DesignSystemTheme {
        DsButton(text = "편집", variant = DsButtonVariant.Secondary, onClick = {})
    }
}

@Preview
@Composable
private fun DsButtonLoadingPreview() {
    DesignSystemTheme {
        DsButton(text = "따라가기", loading = true, onClick = {})
    }
}

@Preview
@Composable
private fun DsButtonDisabledPreview() {
    DesignSystemTheme {
        DsButton(text = "따라가기", enabled = false, onClick = {})
    }
}
