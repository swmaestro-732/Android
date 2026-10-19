package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

private val SheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
private val SheetElevation = 12.dp
private val HandleWidth = 40.dp
private val HandleHeight = 5.dp

/** 손잡이 드래그를 단계 전환으로 볼 최소 이동량(px). 손 떨림을 단계 전환으로 오해하지 않을 정도. */
private const val DRAG_THRESHOLD_PX = 24f

/**
 * 지도 위에 얹히는 하단 시트 껍데기.
 *
 * 시트 높이는 상위가 정한다 — 캐러셀은 내용에 맞춰 늘어나고 리스트는 단계별 고정 높이라
 * 여기서 한 가지로 정할 수 없다. 이 컴포넌트는 모양(둥근 모서리·그림자·손잡이)과 손잡이 제스처만 맡는다.
 *
 * [onExpandedChange] 가 있으면 손잡이를 끌거나 눌러 단계를 바꿀 수 있다. null 이면 손잡이는 장식이다.
 */
@Composable
internal fun MapBottomSheet(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(SheetElevation, SheetShape)
                .clip(SheetShape)
                .background(color.bgDefaultLevel1),
    ) {
        SheetHandle(expanded = expanded, onExpandedChange = onExpandedChange)
        content()
    }
}

@Composable
private fun SheetHandle(
    expanded: Boolean,
    onExpandedChange: ((Boolean) -> Unit)?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(
                    if (onExpandedChange == null) {
                        Modifier
                    } else {
                        Modifier
                            .clickable(onClickLabel = if (expanded) "목록 접기" else "목록 펼치기") {
                                onExpandedChange(!expanded)
                            }.pointerInput(expanded) {
                                var dragDistance = 0f
                                detectVerticalDragGestures(
                                    onDragStart = { dragDistance = 0f },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        dragDistance += dragAmount
                                    },
                                    onDragEnd = {
                                        if (dragDistance < -DRAG_THRESHOLD_PX && !expanded) onExpandedChange(true)
                                        if (dragDistance > DRAG_THRESHOLD_PX && expanded) onExpandedChange(false)
                                    },
                                )
                            }
                    },
                ).padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = HandleWidth, height = HandleHeight)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(color.borderDefaultLevel1),
        )
    }
}

/** 시트 안 좌우 여백. 제목·캐러셀·리스트가 같은 선에 맞도록 한 곳에 둔다. */
internal val SheetHorizontalPadding = 20.dp
