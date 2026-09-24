package com.chillsam.courmy.main.presentation.map.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList

private val ControlShape = RoundedCornerShape(14.dp)
private val ControlSize = 44.dp
private val ControlIconSize = 20.dp
private val ControlElevation = 6.dp

/** 지도 위에 뜨는 흰 사각 버튼(내 위치, 뒤로가기 등). */
@Composable
internal fun MapCircleIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .size(ControlSize)
                .shadow(ControlElevation, ControlShape)
                .clip(ControlShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = color.contentDefaultLevel0,
            modifier = Modifier.size(ControlIconSize),
        )
    }
}

/** 지도 하단 가운데의 어두운 알약 버튼(`목록 보기`). */
@Composable
internal fun MapPillButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier =
            modifier
                .shadow(ControlElevation, shape)
                .clip(shape)
                .background(color.contentDefaultLevel0)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = color.contentOnAccent,
            modifier = Modifier.size(16.dp),
        )
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentOnAccent,
        )
    }
}

/**
 * 지도 위 가로 스크롤 칩 줄.
 *
 * 화면 끝까지 흐르도록 스크롤 컨테이너에 좌우 패딩을 준다 — Row 바깥에 패딩을 주면 스크롤 끝에서
 * 칩이 화면 가장자리에 딱 붙어 잘려 보인다.
 */
@Composable
internal fun MapChipRow(
    labels: ImmutableList<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()).padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            DsChip(
                text = label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
            )
        }
    }
}
