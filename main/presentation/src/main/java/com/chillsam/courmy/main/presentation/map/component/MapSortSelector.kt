package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapSortOrder

/** 정렬 기준 이름. 시트 제목 옆에 그대로 노출된다. */
internal fun MapSortOrder.label(): String =
    when (this) {
        MapSortOrder.SAVED -> "저장순"
        MapSortOrder.DISTANCE -> "거리순"
        MapSortOrder.RATING -> "평점순"
    }

/**
 * 시트 제목 오른쪽의 정렬 선택(`저장순 ▾`).
 *
 * 펼침 여부는 화면 상태가 아니라 이 컴포넌트 안에서만 의미가 있어 UIState 로 올리지 않는다.
 */
@Composable
internal fun MapSortSelector(
    selected: MapSortOrder,
    onSelect: (MapSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier =
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { expanded = true },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            DsText(
                text = selected.label(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down_24),
                contentDescription = null,
                tint = color.contentDefaultLevel2,
                modifier = Modifier.size(14.dp),
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MapSortOrder.entries.forEach { order ->
                DropdownMenuItem(
                    text = {
                        DsText(
                            text = order.label(),
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color =
                                if (order == selected) {
                                    color.contentAccent
                                } else {
                                    color.contentDefaultLevel1
                                },
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelect(order)
                    },
                )
            }
        }
    }
}
