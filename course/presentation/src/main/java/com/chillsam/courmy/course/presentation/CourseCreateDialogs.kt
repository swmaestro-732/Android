package com.chillsam.courmy.course.presentation

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.coroutines.delay

// 코스 만들기 화면의 확인 오버레이(장소 빼기·나가기)와 임시저장 토스트.
// 본문 폼(CourseCreateContent)과 분리해 화면 파일의 관심사를 나눈다.

/** 장소 빼기 확인 바텀시트(Figma FS-34 "⊖ 장소 → 빼기 확인"). 스크림 탭 시 취소. */
@Composable
internal fun RemovePlaceConfirmSheet(
    placeName: String,
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
            DsText(
                text = placeName,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "이 장소를 코스에서 뺄까요?",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
            Spacer(Modifier.height(10.dp))
            SheetActionButton(
                text = "코스에서 빼기",
                textColor = color.contentDanger,
                background = color.bgDefaultLevel1,
                onClick = onConfirm,
            )
            SheetActionButton(
                text = "취소",
                textColor = color.contentDefaultLevel1,
                background = Color.Transparent,
                onClick = onDismiss,
            )
        }
    }
}

/** 나가기 확인 다이얼로그(Figma FS-34 "✕ 닫기 → 나가기 확인"). */
@Composable
internal fun ExitConfirmDialog(
    onSaveAndExit: () -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .noRippleClickable(onCancel),
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
                text = "작성 중인 코스가 있어요",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "지금 나가면 변경사항이 사라져요.\n임시저장하고 나갈까요?",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(Modifier.height(12.dp))
            DsButton(text = "임시저장하고 나가기", onClick = onSaveAndExit)
            SheetActionButton(
                text = "저장 안 함",
                textColor = color.contentDanger,
                background = Color.Transparent,
                onClick = onDiscard,
            )
            SheetActionButton(
                text = "취소",
                textColor = color.contentDefaultLevel2,
                background = Color.Transparent,
                onClick = onCancel,
            )
        }
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
                .padding(start = 20.dp, end = 20.dp, bottom = 96.dp),
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
                text = "임시저장했어요",
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
