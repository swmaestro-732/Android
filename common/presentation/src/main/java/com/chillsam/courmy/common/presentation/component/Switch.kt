package com.chillsam.courmy.common.presentation.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

// Figma design_system > TOGGLE 기준값. Off track 은 전용 슬롯 bgSwitchOff(Gray300) 를 사용한다.
private val TrackWidth = 46.dp
private val TrackHeight = 28.dp
private val ThumbSize = 22.dp
private val ThumbPadding = 3.dp
private const val DISABLED_ALPHA = 0.4f
private const val PILL_PERCENT = 50

/**
 * 온/오프 토글. Figma `design_system > TOGGLE`.
 * - On: 강조 트랙([DesignSystemSemanticColors.bgAccent])
 * - Off: off 트랙([DesignSystemSemanticColors.bgSwitchOff])
 */
@Composable
fun DsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val trackColor =
        if (checked) {
            DesignSystemThemeImpl.designSystemColor.bgAccent
        } else {
            DesignSystemThemeImpl.designSystemColor.bgSwitchOff
        }
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - ThumbPadding else ThumbPadding,
        label = "thumbOffset",
    )

    Box(
        modifier =
            modifier
                .alpha(if (enabled) 1f else DISABLED_ALPHA)
                .size(width = TrackWidth, height = TrackHeight)
                .clip(RoundedCornerShape(percent = PILL_PERCENT))
                .background(trackColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = { onCheckedChange(!checked) },
                ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .offset(x = thumbOffset)
                    .size(ThumbSize)
                    .clip(CircleShape)
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
        )
    }
}

@Preview
@Composable
private fun DsSwitchOnPreview() {
    DesignSystemTheme {
        DsSwitch(checked = true, onCheckedChange = {})
    }
}

@Preview
@Composable
private fun DsSwitchOffPreview() {
    DesignSystemTheme {
        DsSwitch(checked = false, onCheckedChange = {})
    }
}
