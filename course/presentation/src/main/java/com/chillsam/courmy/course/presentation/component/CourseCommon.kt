package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 라운드 사각형 점선 테두리(장소 더 담기 · 사진 추가 슬롯). Compose 기본 border 는 점선 미지원이라 직접 그린다. */
fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
): Modifier =
    drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style =
                Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx())),
                ),
        )
    }

/** "① 코스 정보" 형태의 번호 섹션 헤더. */
@Composable
fun CourseSectionHeader(
    number: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = number.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
            )
        }
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
    }
}

/** 상단바: ✕ 닫기 · 새 코스 만들기 · 임시저장. */
@Composable
fun CourseTopBar(
    onClose: () -> Unit,
    onSaveDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                    .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "✕",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            )
        }
        DsText(
            text = "새 코스 만들기",
            modifier = Modifier.weight(1f),
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        DsText(
            text = "임시저장",
            modifier = Modifier.clickable(onClick = onSaveDraft).padding(6.dp),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
    }
}

/** 장소 사이 경로 커넥터: "도보 9분 · 경로 자동". */
@Composable
fun CourseRouteConnector(
    text: String,
    modifier: Modifier = Modifier,
) {
    DsText(
        text = "＋ $text",
        modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
    )
}
