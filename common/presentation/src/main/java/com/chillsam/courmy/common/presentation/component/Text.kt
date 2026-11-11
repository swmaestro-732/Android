package com.chillsam.courmy.common.presentation.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.chillsam.courmy.common.presentation.translation.rememberAutoTranslated
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@Composable
fun DsText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Visible,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    // 줄 수를 미리 확보해 높이를 고정하고 싶은 자리(목록 카드 제목 등)에서 쓴다.
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    // 서버 게시글 등 **동적 콘텐츠**에서만 true 로 켜 온디바이스 AI 번역을 돌린다.
    // 기본 false — 앱 고정 UI 문자열은 번역하지 않아 오번역을 막는다(고정 문구는 strings.xml 로 번역).
    autoTranslate: Boolean = false,
) {
    Text(
        text = rememberAutoTranslated(text, enabled = autoTranslate),
        modifier = modifier,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines.coerceAtLeast(minLines),
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}
