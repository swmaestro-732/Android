package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

// Figma design_system > TEXT INPUT 기준값.
private val TextFieldHeight = 46.dp
private val TextFieldCornerRadius = 12.dp
private val TextFieldHorizontalPadding = 14.dp
private val BorderWidthDefault = 1.dp
private val BorderWidthActive = 1.5.dp
private val FocusRingWidth = 3.dp
private const val FOCUS_RING_ALPHA = 0.12f

/**
 * 포커스 링이 만드는 안쪽 여백을 상쇄해, 입력창 **테두리**를 부모의 좌우 끝선에 맞춘다.
 *
 * [DsTextField] 는 링 자리로 좌우 [FocusRingWidth] 씩을 늘 비워 둔다(포커스 때 크기가 튀지 않게).
 * 그래서 같은 컨테이너 안에서 라벨·칩과 나란히 두면 입력창만 양쪽으로 3dp 들어가 보인다.
 * 이 modifier 는 자식을 그만큼 넓게 재고 왼쪽으로 당겨 놓되, 부모에게는 원래 폭으로 보고해
 * 주변 요소의 자리는 건드리지 않는다.
 */
fun Modifier.alignTextFieldBorder(): Modifier =
    layout { measurable, constraints ->
        val bleed = FocusRingWidth.roundToPx()
        val placeable = measurable.measure(constraints.offset(horizontal = bleed * 2))
        layout(placeable.width - bleed * 2, placeable.height) {
            placeable.place(-bleed, 0)
        }
    }

/**
 * 단일 라인 입력 필드. Figma `design_system > TEXT INPUT` 의 3상태.
 * - Default: 옅은 배경 + 기본 테두리
 * - Focus: 흰 배경 + 강조 테두리([DesignSystemSemanticColors.borderAccent]) + 12% 글로우 링
 * - Error([isError]=true): 흰 배경 + 위험 테두리([DesignSystemSemanticColors.borderDanger]) + 위험색 텍스트
 *
 * 글로우 링은 borderAccent 의 12% alpha 파생(신규 팔레트 없음). unfocused 시 투명이라 레이아웃 고정.
 */
@Composable
fun DsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val colors = textFieldColors(focused = focused, isError = isError)
    val borderWidth = if (focused || isError) BorderWidthActive else BorderWidthDefault
    val shape = RoundedCornerShape(TextFieldCornerRadius)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.ring)
                .padding(FocusRingWidth),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (singleLine) {
                            Modifier.height(TextFieldHeight)
                        } else {
                            Modifier.heightIn(min = TextFieldHeight)
                        },
                    ).clip(shape)
                    .background(colors.background)
                    .border(borderWidth, colors.border, shape)
                    .padding(
                        horizontal = TextFieldHorizontalPadding,
                        vertical = if (singleLine) 0.dp else 12.dp,
                    ),
            enabled = enabled,
            singleLine = singleLine,
            interactionSource = interactionSource,
            textStyle = DesignSystemThemeImpl.typeScale.textRegularS.copy(color = colors.text),
            cursorBrush = SolidColor(DesignSystemThemeImpl.designSystemColor.borderAccent),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(Modifier.width(8.dp))
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
                    ) {
                        if (value.isEmpty()) DsTextFieldPlaceholder(placeholder)
                        innerTextField()
                    }
                }
            },
        )
    }
}

private class DsTextFieldColors(
    val background: Color,
    val border: Color,
    val text: Color,
    val ring: Color,
)

@Composable
private fun textFieldColors(
    focused: Boolean,
    isError: Boolean,
): DsTextFieldColors {
    val color = DesignSystemThemeImpl.designSystemColor
    return DsTextFieldColors(
        // 입력 필드 배경은 항상 흰색(빈 상태에서도).
        background = color.bgDefaultLevel1,
        border =
            when {
                isError -> color.borderDanger
                focused -> color.borderAccent
                else -> color.borderDefaultLevel0
            },
        text = if (isError) color.contentDanger else color.contentDefaultLevel0,
        ring =
            if (focused && !isError) {
                color.borderAccent.copy(alpha = FOCUS_RING_ALPHA)
            } else {
                Color.Transparent
            },
    )
}

@Composable
private fun DsTextFieldPlaceholder(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textRegularS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview
@Composable
private fun DsTextFieldDefaultPreview() {
    DesignSystemTheme {
        DsTextField(value = "", onValueChange = {}, placeholder = "코스 이름 입력")
    }
}

@Preview
@Composable
private fun DsTextFieldFilledPreview() {
    DesignSystemTheme {
        DsTextField(value = "성수 비 오는 날", onValueChange = {})
    }
}

@Preview
@Composable
private fun DsTextFieldErrorPreview() {
    DesignSystemTheme {
        DsTextField(value = "", onValueChange = {}, placeholder = "이름을 입력해 주세요", isError = true)
    }
}
