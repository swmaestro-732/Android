package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 뒤 화면을 덮는 스크림 농도. */
private const val SCRIM_ALPHA = 0.4f

/** 카드가 화면 좌우 끝에 닿지 않게 두는 여백. */
private val DialogSideMargin = 40.dp

private val DialogCornerRadius = 20.dp
private val DialogButtonHeight = 52.dp
private val DialogButtonCornerRadius = 16.dp

/**
 * 앱 공용 확인 다이얼로그.
 *
 * 형식을 하나로 고정한다 — **질문(제목) → 설명 → 네/아니요**.
 * 화면마다 "저장 취소/저장 유지", "삭제하기/취소" 처럼 버튼 라벨이 제각각이면 사용자가 매번 읽고
 * 판단해야 한다. 라벨을 네/아니요로 고정하면 판단은 제목 한 줄에서 끝난다.
 * 그래서 [title] 은 반드시 **예/아니요로 답할 수 있는 질문**이어야 한다.
 *
 * [destructive] 는 되돌릴 수 없는 동작(탈퇴·삭제)일 때 켠다. "네" 버튼이 danger 색으로 바뀐다.
 */
@Composable
fun DsConfirmDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "네",
    dismissText: String = "아니요",
    destructive: Boolean = false,
) {
    DsDialogScaffold(title = title, description = description, onDismiss = onDismiss) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val color = DesignSystemThemeImpl.designSystemColor
            // 취소를 왼쪽에 둔다. 오른쪽(엄지 쪽)에 놓으면 실수로 누르기 쉽다.
            DialogButton(
                text = dismissText,
                textColor = color.contentDefaultLevel1,
                background = Color.Transparent,
                borderColor = color.borderDefaultLevel1,
                onClick = onDismiss,
            )
            DialogButton(
                text = confirmText,
                textColor = color.contentOnAccent,
                background = if (destructive) color.contentDanger else color.bgAccent,
                borderColor = null,
                onClick = onConfirm,
            )
        }
    }
}

/**
 * 확인 다이얼로그와 같은 껍데기(스크림 + 중앙 카드 + 질문/설명)에 버튼만 직접 채우는 형태.
 *
 * 선택지가 셋 이상이라 네/아니요로 못 줄이는 경우에만 쓴다. 두 갈래면 [DsConfirmDialog] 를 쓴다.
 */
@Composable
fun DsDialogScaffold(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    actions: @Composable ColumnScope.() -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = SCRIM_ALPHA))
                    .noRippleClickable(onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = DialogSideMargin)
                        .clip(RoundedCornerShape(DialogCornerRadius))
                        .background(color.bgDefaultLevel1)
                        // 카드 안을 눌렀을 때 스크림까지 전달돼 닫히지 않게 여기서 소비한다.
                        .noRippleClickable {}
                        .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsText(
                    text = title,
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                )
                DsText(
                    text = description,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                )
                Spacer(Modifier.height(12.dp))
                actions()
            }
        }
    }
}

/** 다이얼로그 버튼. [borderColor] 가 있으면 테두리만 있는 보조 버튼으로 그린다. */
@Composable
private fun RowScope.DialogButton(
    text: String,
    textColor: Color,
    background: Color,
    borderColor: Color?,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(DialogButtonCornerRadius)
    Box(
        modifier =
            Modifier
                .weight(1f)
                .height(DialogButtonHeight)
                .clip(shape)
                .background(background)
                .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = textColor,
        )
    }
}

/** 리플 없는 클릭(스크림·카드 배경 소비용). */
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    }
