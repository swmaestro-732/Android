package com.chillsam.courmy.course.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsBilingualText
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsDialogScaffold
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import kotlinx.coroutines.delay

// 코스 만들기 화면의 확인 오버레이(장소 빼기·나가기)와 임시저장 토스트.
// 본문 폼(CourseCreateContent)과 분리해 화면 파일의 관심사를 나눈다.

/** 장소 빼기 확인 바텀시트(Figma FS-34 "⊖ 장소 → 빼기 확인"). 스크림 탭 시 취소. */
@Composable
internal fun RemovePlaceConfirmSheet(
    placeName: String,
    isPlan: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .noRippleClickable(onDismiss),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(color.bgDefaultLevel0)
                    .noRippleClickable {}
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DsBilingualText(
                text = placeName,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text =
                    if (isPlan) {
                        stringResource(R.string.course_remove_place_question_plan)
                    } else {
                        stringResource(R.string.course_remove_place_question_course)
                    },
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
            Spacer(Modifier.height(10.dp))
            SheetActionButton(
                text =
                    if (isPlan) {
                        stringResource(R.string.course_remove_place_confirm_plan)
                    } else {
                        stringResource(R.string.course_remove_place_confirm_course)
                    },
                textColor = color.contentDanger,
                background = color.bgDefaultLevel1,
                onClick = onConfirm,
            )
            SheetActionButton(
                text = stringResource(R.string.course_dialog_cancel),
                textColor = color.contentDefaultLevel1,
                background = Color.Transparent,
                onClick = onDismiss,
            )
        }
    }
}

/**
 * 나가기 확인 다이얼로그(Figma FS-34 "✕ 닫기 → 나가기 확인").
 *
 * 여기만 선택지가 셋(임시저장·저장 안 함·취소)이라 네/아니요로 줄일 수 없다. 껍데기와 문구 형식은
 * [DsConfirmDialog] 와 같게 맞추고 버튼만 직접 채운다.
 */
@Composable
internal fun ExitConfirmDialog(
    onSaveAndExit: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    DsDialogScaffold(
        title = stringResource(R.string.course_exit_confirm_title),
        description = stringResource(R.string.course_exit_confirm_desc),
        onDismiss = onCancel,
    ) {
        DsButton(text = stringResource(R.string.course_exit_save_and_exit), onClick = onSaveAndExit)
        SheetActionButton(
            text = stringResource(R.string.course_exit_discard),
            textColor = color.contentDanger,
            background = Color.Transparent,
            onClick = onDiscard,
        )
        SheetActionButton(
            text = stringResource(R.string.course_dialog_cancel),
            textColor = color.contentDefaultLevel2,
            background = Color.Transparent,
            onClick = onCancel,
        )
    }
}

/** 임시저장 완료 토스트. 잠시 노출 후 자동으로 사라진다. */
@Composable
internal fun SavedDraftToast(
    visible: Boolean,
    onHidden: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!visible) return
    LaunchedEffect(Unit) {
        delay(TOAST_DURATION_MILLIS)
        onHidden()
    }
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = ScreenHorizontalPadding, end = ScreenHorizontalPadding, bottom = 96.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            DsText(
                text = stringResource(R.string.course_saved_draft_toast),
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
            )
        }
    }
}

@Composable
private fun SheetActionButton(
    text: String,
    textColor: Color,
    background: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = textColor,
        )
    }
}

/** 리플·인디케이션 없는 클릭(스크림/시트 배경 소비용). */
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    }

private const val TOAST_DURATION_MILLIS = 2000L

/** 코스 삭제 확인. 되돌릴 수 없는 동작이라 "네" 를 danger 로 둔다. */
@Composable
internal fun CourseDeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    DsConfirmDialog(
        title = stringResource(R.string.course_delete_confirm_title),
        description = stringResource(R.string.course_delete_confirm_desc),
        destructive = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

/**
 * 새로운 위치 추가 다이얼로그.
 *
 * 등록된 장소 검색으로 찾지 못한 곳을 이름으로 직접 찾아 담는다.
 * 확인을 누르면 외부 지도 검색(`GET /api/v1/places/search`)을 호출한다.
 */
@Composable
internal fun AddPlaceDialog(
    isSearching: Boolean,
    errorMessage: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var name by remember { mutableStateOf("") }
    val canSubmit = name.isNotBlank() && !isSearching
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // 커스텀 오버레이라 시스템 Back 이 화면을 이탈시키지 않고 다이얼로그만 닫도록 가로챈다.
    BackHandler(onBack = onDismiss)
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .noRippleClickable(onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.bgDefaultLevel0)
                    .noRippleClickable {}
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsText(
                text = stringResource(R.string.course_add_place_title),
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = stringResource(R.string.course_add_place_description),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(Modifier.height(4.dp))
            DsTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.course_add_place_name_placeholder),
                enabled = !isSearching,
                modifier = Modifier.focusRequester(focusRequester),
            )
            if (errorMessage != null) {
                DsText(
                    text = errorMessage,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDanger,
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                )
            }
            Spacer(Modifier.height(4.dp))
            DsButton(
                text =
                    if (isSearching) {
                        stringResource(R.string.course_add_place_searching)
                    } else {
                        stringResource(R.string.course_add_place_search)
                    },
                enabled = canSubmit,
                onClick = { onConfirm(name.trim()) },
            )
            SheetActionButton(
                text = stringResource(R.string.course_dialog_cancel),
                textColor = color.contentDefaultLevel2,
                background = Color.Transparent,
                onClick = onDismiss,
            )
        }
    }
}
