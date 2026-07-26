package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList

/** ③ 코스 설정 — 태그 카드. 선택 태그칩 · 입력행 · 추천 태그를 한 카드에 담는다. */
@Composable
fun CourseTagCard(
    tags: ImmutableList<String>,
    suggestedTags: ImmutableList<String>,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .border(
                    1.dp,
                    DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
                    RoundedCornerShape(18.dp),
                ).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldLabel("태그")
        SelectedTags(tags = tags, onRemoveTag = onRemoveTag)
        TagInputRow(onAddTag = onAddTag)
        FieldLabel("추천 태그")
        SuggestedTags(suggestedTags = suggestedTags, onAddTag = onAddTag)
    }
}

/** 선택된 태그 칩 목록(개별 삭제 가능). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SelectedTags(
    tags: ImmutableList<String>,
    onRemoveTag: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        tags.forEach { tag ->
            RemovableTagChip(tag = tag, onRemove = { onRemoveTag(tag) })
        }
    }
}

@Composable
private fun RemovableTagChip(
    tag: String,
    onRemove: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(DesignSystemThemeImpl.designSystemColor.bgAccent)
                .padding(start = 13.dp, end = 11.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DsText(
            text = tag,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
        )
        DsText(
            text = "✕",
            modifier = Modifier.clickable(onClick = onRemove),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
        )
    }
}

/** 태그 입력 필드 + 추가 버튼. */
@Composable
internal fun TagInputRow(onAddTag: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)
                .border(
                    1.dp,
                    if (focused) {
                        DesignSystemThemeImpl.designSystemColor.borderAccent
                    } else {
                        DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0
                    },
                    RoundedCornerShape(12.dp),
                ).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(
            text = "＋",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        )
        TagInputField(
            text = text,
            onTextChange = { text = it },
            onFocusChanged = { focused = it },
            modifier = Modifier.weight(1f),
        )
        AddTagButton(
            onClick = {
                if (text.isNotBlank()) {
                    onAddTag(text)
                    text = ""
                }
            },
        )
    }
}

@Composable
private fun TagInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.onFocusChanged { onFocusChanged(it.isFocused) },
        singleLine = true,
        textStyle =
            DesignSystemThemeImpl.typeScale.textRegularXS
                .copy(color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0),
        cursorBrush = SolidColor(DesignSystemThemeImpl.designSystemColor.borderAccent),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                DsText(
                    text = "태그 추가",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
                )
            }
            innerTextField()
        },
    )
}

@Composable
private fun AddTagButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(DesignSystemThemeImpl.designSystemColor.bgAccent)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        DsText(
            text = "추가",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
        )
    }
}

/** 추천 태그 칩 목록(탭하면 선택 태그로 추가). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SuggestedTags(
    suggestedTags: ImmutableList<String>,
    onAddTag: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        suggestedTags.forEach { tag ->
            SuggestedTagChip(tag = tag, onClick = { onAddTag(tag) })
        }
    }
}

@Composable
private fun SuggestedTagChip(
    tag: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .border(
                    1.dp,
                    DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
                    RoundedCornerShape(percent = 50),
                ).clickable(onClick = onClick)
                .padding(horizontal = 13.dp, vertical = 8.dp),
    ) {
        DsText(
            text = "+ $tag",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
        )
    }
}
