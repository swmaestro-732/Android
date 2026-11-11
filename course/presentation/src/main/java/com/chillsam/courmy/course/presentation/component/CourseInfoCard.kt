package com.chillsam.courmy.course.presentation.component

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList
import java.time.LocalDate
import com.chillsam.courmy.course.presentation.R as CourseR

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
                .cardShadow(RoundedCornerShape(18.dp))
                .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        var nameFocused by remember { mutableStateOf(false) }
        var descriptionFocused by remember { mutableStateOf(false) }

        FieldLabel(stringResource(CourseR.string.course_info_name_label))
        InlineField(
            value = name,
            onValueChange = actions.onNameChange,
            placeholder = stringResource(CourseR.string.course_info_name_placeholder),
            textStyle = DesignSystemThemeImpl.typeScale.textStrongM,
            onFocusChanged = { nameFocused = it },
        )
        Divider(focused = nameFocused)
        FieldLabel(stringResource(CourseR.string.course_info_description_label))
        InlineField(
            value = description,
            onValueChange = actions.onDescriptionChange,
            placeholder = stringResource(CourseR.string.course_info_description_placeholder),
            textStyle = DesignSystemThemeImpl.typeScale.textRegularS,
            singleLine = false,
            onFocusChanged = { descriptionFocused = it },
        )
        Divider(focused = descriptionFocused)
        FieldLabel(stringResource(CourseR.string.course_info_thumbnail_label))
        // 썸네일은 1장만 선택하므로 "n/max" 개수 표시를 숨긴다.
        CoursePhotoRow(
            photos = thumbnailPhotos,
            maxPhotos = thumbnailMaxPhotos,
            onPhotosChange = actions.onThumbnailPhotosChange,
            showCount = false,
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

/** 계획의 선택 입력. 공개 코스 전용 사진·태그·공개범위는 받지 않는다. */
@Composable
fun PlanInfoCard(
    title: String,
    memo: String,
    plannedDate: String,
    onTitleChange: (String) -> Unit,
    onMemoChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().cardShadow(RoundedCornerShape(18.dp)).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldLabel(stringResource(CourseR.string.course_plan_title_label, title.length))
        InlineField(
            value = title,
            onValueChange = { onTitleChange(it.take(200)) },
            placeholder = stringResource(CourseR.string.course_plan_title_placeholder),
            textStyle = DesignSystemThemeImpl.typeScale.textStrongM,
        )
        Divider()
        FieldLabel(stringResource(CourseR.string.course_plan_memo_label, memo.length))
        InlineField(
            value = memo,
            onValueChange = { onMemoChange(it.take(500)) },
            placeholder = stringResource(CourseR.string.course_plan_memo_placeholder),
            textStyle = DesignSystemThemeImpl.typeScale.textRegularS,
            singleLine = false,
        )
        Divider()
        FieldLabel(stringResource(CourseR.string.course_plan_date_label))
        PlannedDateField(plannedDate = plannedDate, onDateChange = onDateChange)
    }
}

/**
 * 방문 예정일 선택 줄. 탭하면 네이티브 날짜 선택 다이얼로그가 열리고 고른 날짜(yyyy-MM-dd)를 돌려준다.
 * 손으로 형식을 맞춰 적을 필요 없이 달력에서 고르게 한다. 날짜가 있으면 아래에 "날짜 미정으로 변경"을 둔다.
 * 계획 작성 카드와 계획 상세 수정 다이얼로그가 같이 쓴다.
 */
@Composable
internal fun PlannedDateField(
    plannedDate: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        DsText(
            text = plannedDate.ifEmpty { stringResource(CourseR.string.course_plan_date_unset) },
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        val initialDate = runCatching { LocalDate.parse(plannedDate) }.getOrDefault(LocalDate.now())
                        DatePickerDialog(
                            context,
                            { _, year, month, day -> onDateChange(LocalDate.of(year, month + 1, day).toString()) },
                            initialDate.year,
                            initialDate.monthValue - 1,
                            initialDate.dayOfMonth,
                        ).show()
                    }.padding(vertical = 12.dp),
        )
        if (plannedDate.isNotEmpty()) {
            DsText(
                text = stringResource(CourseR.string.course_plan_date_clear),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
                modifier = Modifier.clickable { onDateChange("") }.padding(vertical = 12.dp),
            )
        }
    }
}
