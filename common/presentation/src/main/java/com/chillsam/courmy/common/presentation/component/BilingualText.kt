package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.chillsam.courmy.common.presentation.translation.rememberAutoTranslated
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 장소명·주소 등 동적 정보의 원문을 유지하고, 다른 번역 결과가 있을 때만 아래에 함께 표시한다. */
@Composable
fun DsBilingualText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Visible,
) {
    val translated = rememberAutoTranslated(text, enabled = true)
    Column(modifier = modifier) {
        DsText(text = text, style = style, color = color, maxLines = maxLines, overflow = overflow)
        if (translated.isNotBlank() && translated != text) {
            DsText(
                text = translated,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color,
                maxLines = maxLines,
                overflow = overflow,
            )
        }
    }
}
