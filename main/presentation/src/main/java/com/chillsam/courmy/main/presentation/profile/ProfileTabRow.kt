package com.chillsam.courmy.main.presentation.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val IndicatorHeight = 2.dp
private val TabVerticalPadding = 14.dp

/**
 * 프로필 코스 목록 위의 구분 탭.
 *
 * 칩(DsChip)이 아니라 밑줄 탭인 이유는, 칩은 "여러 개를 골라 걸러내는" 필터로 이미 쓰이고 있어서다.
 * 여기서는 목록 자체가 통째로 바뀌므로 한 번에 하나만 켜지는 게 분명한 밑줄 탭이 맞다.
 *
 * 탭 폭은 개수에 상관없이 균등 분할한다 — 글자 길이에 따라 폭이 달라지면 탭을 옮길 때마다
 * 밑줄 위치가 들쭉날쭉해 보인다.
 */
@Composable
fun ProfileTabRow(
    tabs: ImmutableList<ProfileTabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        tabs.forEachIndexed { index, tab ->
            ProfileTabCell(
                tab = tab,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileTabCell(
    tab: ProfileTabItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val contentColor by animateColorAsState(
        targetValue = if (selected) color.contentDefaultLevel0 else color.contentDefaultLevel3,
        label = "profileTabContent",
    )
    Column(
        modifier =
            modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.padding(vertical = TabVerticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DsText(
                text = tab.label,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = contentColor,
            )
            DsText(
                text = tab.count.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                // 개수는 이름보다 한 단계 흐리게 둬 제목이 먼저 읽히게 한다.
                color = if (selected) color.contentDefaultLevel2 else color.contentDefaultLevel3,
            )
        }
        // 밑줄은 항상 자리를 차지하고 색만 바뀐다. 선택할 때 탭 높이가 흔들리지 않게 하려는 것.
        // 선택 안 된 칸도 옅은 선을 그려, 두 칸이 이어져 한 줄 위에 강조 표시가 얹힌 것처럼 보인다.
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IndicatorHeight)
                    .background(if (selected) color.contentAccent else color.borderDefaultLevel1),
        )
    }
}

@Preview
@Composable
private fun ProfileTabRowPreview() {
    DesignSystemTheme {
        Column {
            ProfileTabRow(
                tabs =
                    persistentListOf(
                        ProfileTabItem("계획", 3),
                        ProfileTabItem("코스", 8),
                    ),
                selectedIndex = 1,
                onSelect = {},
            )
        }
    }
}
