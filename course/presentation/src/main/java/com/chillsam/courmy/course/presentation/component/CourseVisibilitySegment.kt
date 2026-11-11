package com.chillsam.courmy.course.presentation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseVisibility
import com.chillsam.courmy.course.presentation.R

private data class VisibilityOption(
    val visibility: CourseVisibility,
    @StringRes val labelRes: Int,
    @StringRes val helperRes: Int,
)

private val VISIBILITY_OPTIONS =
    listOf(
        VisibilityOption(
            CourseVisibility.PUBLIC,
            R.string.course_visibility_public_label,
            R.string.course_visibility_public_helper,
        ),
        VisibilityOption(
            CourseVisibility.FOLLOWER,
            R.string.course_visibility_follower_label,
            R.string.course_visibility_follower_helper,
        ),
        VisibilityOption(
            CourseVisibility.PRIVATE,
            R.string.course_visibility_private_label,
            R.string.course_visibility_private_helper,
        ),
    )

/** ③ 공개 설정 — 공개/팔로워/비공개 세그먼트 + 헬퍼 텍스트. */
@Composable
fun CourseVisibilitySegment(
    selected: CourseVisibility,
    onSelect: (CourseVisibility) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)
                    .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            VISIBILITY_OPTIONS.forEach { option ->
                SegmentCell(
                    label = stringResource(option.labelRes),
                    selected = option.visibility == selected,
                    onClick = { onSelect(option.visibility) },
                )
            }
        }
        DsText(
            text = stringResource(VISIBILITY_OPTIONS.first { it.visibility == selected }.helperRes),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
    }
}

@Composable
private fun RowScope.SegmentCell(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val cellModifier =
        if (selected) {
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(DesignSystemThemeImpl.designSystemColor.bgAccent)
        } else {
            Modifier.weight(1f)
        }
    DsText(
        text = label,
        modifier =
            cellModifier
                .clickable(onClick = onClick)
                .padding(vertical = 11.dp),
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color =
            if (selected) {
                DesignSystemThemeImpl.designSystemColor.contentOnAccent
            } else {
                DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1
            },
        textAlign = TextAlign.Center,
    )
}
