package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 코스 댓글. 서버 계약 전에는 ViewModel의 화면 세션 상태만 표시한다. */
@Composable
internal fun CourseSocialSection(
    state: CourseSocialUIState,
    onCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val commentDraft = state.commentDraft
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        DsText(
            text = "댓글 ${state.comments.size}",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            DsTextField(
                value = commentDraft,
                onValueChange = onCommentChange,
                placeholder = "댓글을 남겨 주세요",
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            DsText(
                text = "등록",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = if (commentDraft.isBlank()) color.contentDefaultLevel3 else color.contentAccent,
                modifier =
                    Modifier
                        .clickable(enabled = commentDraft.isNotBlank(), onClick = onSubmitComment)
                        .padding(8.dp),
            )
        }
        if (state.comments.isEmpty()) {
            DsText(
                text = "첫 댓글을 남겨 보세요.",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel3,
            )
        } else {
            state.comments.forEach { comment ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DsText(
                            text = comment.author,
                            style = DesignSystemThemeImpl.typeScale.textStrongS,
                            color = color.contentDefaultLevel0,
                        )
                        Spacer(Modifier.width(8.dp))
                        DsText(
                            text = comment.dateText,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentDefaultLevel3,
                        )
                    }
                    DsText(
                        text = comment.body,
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel1,
                        maxLines = Int.MAX_VALUE,
                    )
                }
            }
        }
    }
}
