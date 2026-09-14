package com.chillsam.courmy.main.presentation.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

private val BarShape = RoundedCornerShape(percent = 50)
private val IconSize = 18.dp

/**
 * 검색어 입력칸(FS-17). 화면에 들어오면 곧바로 포커스를 잡아 키보드를 올린다 — 검색하러 온 화면이라
 * 한 번 더 눌러 입력을 시작하게 만들 이유가 없다.
 *
 * 테두리를 강조색으로 두는 건 시안의 포커스 상태를 그대로 옮긴 것이다.
 */
@Composable
internal fun SearchInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(autoFocus) {
        if (autoFocus) focusRequester.requestFocus()
    }

    Row(
        modifier =
            modifier
                .clip(BarShape)
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderAccent, shape = BarShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_24),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.size(IconSize),
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f).focusRequester(focusRequester),
            singleLine = true,
            textStyle =
                DesignSystemThemeImpl.typeScale.textRegularS.copy(color = color.contentDefaultLevel0),
            cursorBrush = SolidColor(color.contentAccent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    DsText(
                        text = "장소 · 동네 · 테마 검색",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel3,
                    )
                }
                innerTextField()
            },
        )
        if (query.isNotEmpty()) {
            Icon(
                painter = painterResource(R.drawable.close_small_24),
                contentDescription = "검색어 지우기",
                tint = color.contentDefaultLevel2,
                modifier =
                    Modifier
                        .size(IconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClear,
                        ),
            )
        }
    }
}
