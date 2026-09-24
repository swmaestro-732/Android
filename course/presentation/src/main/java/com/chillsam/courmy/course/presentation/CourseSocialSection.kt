package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding

/** 댓글 시트가 차지할 화면 높이 비율. 뒤의 코스가 살짝 비쳐 "덮인 것"으로 읽히게 다 채우지 않는다. */
private const val COMMENT_SHEET_HEIGHT_FRACTION = 0.72f

/**
 * 댓글 하단 시트. 하단 액션바의 댓글 버튼으로 연다.
 *
 * 댓글을 본문에 깔지 않고 시트로 뺀 이유 — 코스 상세는 장소·지도·후기로 이미 길다. 댓글까지
 * 이어 붙이면 코스를 다 훑은 뒤에야 닿고, 반대로 댓글만 보려는 사람은 한참 스크롤해야 한다.
 *
 * 컨테이너로 `ModalBottomSheet` 를 쓴다. 장소 시트
 * ([com.chillsam.courmy.course.presentation.component.PlaceDetailSheet])가 이걸 피하고 [androidx.compose.ui.window.Dialog]
 * 로 직접 만든 건 **시트 안에 지도가 있어서**다(시트 전체가 draggable 이라 지도 팬이 죽었다).
 * 댓글 시트에는 지도가 없으므로 그 문제가 없고, 표준 컨테이너가 끌어내리기·스크림·뒤로가기를 다 해 준다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CourseCommentSheet(
    state: CourseSocialUIState,
    onCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // 절반 높이를 거치지 않고 바로 올라온다. 댓글은 중간 단계에서 보여 줄 요약이 없다.
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = color.bgDefaultLevel0,
    ) {
        CourseSocialSection(
            state = state,
            onCommentChange = onCommentChange,
            onSubmitComment = onSubmitComment,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(COMMENT_SHEET_HEIGHT_FRACTION)
                    // 키보드가 올라오면 입력줄이 가려지므로 그만큼 밀어 올린다.
                    .imePadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding)
                    .padding(bottom = 16.dp),
        )
    }
}

/** 코스 댓글. 서버 계약 전에는 ViewModel의 화면 세션 상태만 표시한다. */
@Composable
internal fun CourseSocialSection(
    state: CourseSocialUIState,
    onCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val commentDraft = state.commentDraft
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
