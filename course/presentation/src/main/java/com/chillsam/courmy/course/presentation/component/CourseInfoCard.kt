package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList

/** ① 코스 정보 카드 — 이름/설명 인라인 입력 · 태그칩 · 추천 태그. */
@Composable
fun CourseInfoCard(
    name: String,
    description: String,
    tags: ImmutableList<String>,
    suggestedTags: ImmutableList<String>,
    actions: CourseInfoCardActions,
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
        FieldLabel("코스 이름")
        InlineField(
            value = name,
            onValueChange = actions.onNameChange,
            placeholder = "코스 이름을 입력하세요",
            textStyle = DesignSystemThemeImpl.typeScale.textStrongM,
        )
        Divider()
        FieldLabel("코스 설명")
        InlineField(
            value = description,
            onValueChange = actions.onDescriptionChange,
            placeholder = "코스 설명을 입력하세요",
            textStyle = DesignSystemThemeImpl.typeScale.textRegularS,
            singleLine = false,
        )
        Divider()
        FieldLabel("태그")
        SelectedTags(tags = tags, onRemoveTag = actions.onRemoveTag)
        TagInputRow(onAddTag = actions.onAddTag)
        FieldLabel("추천 태그")
        SuggestedTags(suggestedTags = suggestedTags, onAddTag = actions.onAddTag)
    }
}

@Composable
private fun FieldLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
    )
}

@Composable
private fun Divider() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0),
    )
}

@Composable
private fun InlineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    singleLine: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        textStyle = textStyle.copy(color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0),
        cursorBrush = SolidColor(DesignSystemThemeImpl.designSystemColor.borderAccent),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                DsText(
                    text = placeholder,
                    style = textStyle,
                    color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
                    maxLines = Int.MAX_VALUE,
                )
            }
            innerTextField()
        },
    )
}
