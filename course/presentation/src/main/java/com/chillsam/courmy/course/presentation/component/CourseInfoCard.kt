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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList

/** ① 코스 정보 카드 — 이름/설명 인라인 입력 · 썸네일 사진. */
@Composable
fun CourseInfoCard(
    name: String,
    description: String,
    thumbnailPhotos: ImmutableList<String>,
    thumbnailMaxPhotos: Int,
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
        var nameFocused by remember { mutableStateOf(false) }
        var descriptionFocused by remember { mutableStateOf(false) }

        FieldLabel("코스 이름")
        InlineField(
            value = name,
            onValueChange = actions.onNameChange,
            placeholder = "코스 이름을 입력하세요",
            textStyle = DesignSystemThemeImpl.typeScale.textStrongM,
            onFocusChanged = { nameFocused = it },
        )
        Divider(focused = nameFocused)
        FieldLabel("코스 설명")
        InlineField(
            value = description,
            onValueChange = actions.onDescriptionChange,
            placeholder = "코스 설명을 입력하세요",
            textStyle = DesignSystemThemeImpl.typeScale.textRegularS,
            singleLine = false,
            onFocusChanged = { descriptionFocused = it },
        )
        Divider(focused = descriptionFocused)
        FieldLabel("썸네일 이미지")
        CoursePhotoRow(
            photos = thumbnailPhotos,
            maxPhotos = thumbnailMaxPhotos,
            onPhotosChange = actions.onThumbnailPhotosChange,
        )
    }
}

@Composable
private fun Divider(focused: Boolean = false) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    if (focused) {
                        DesignSystemThemeImpl.designSystemColor.borderAccent
                    } else {
                        DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0
                    },
                ),
    )
}

@Composable
private fun InlineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textStyle: TextStyle,
    singleLine: Boolean = true,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().onFocusChanged { onFocusChanged(it.isFocused) },
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
