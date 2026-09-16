package com.chillsam.courmy.main.presentation.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

internal val BottomBarHeight = 60.dp
private val TabIconSize = 24.dp

/**
 * 앱 공용 하단 탭바. 홈·마이 등 하단 네비게이션 목적지에서 공유한다.
 * 프로토타입 단계라 홈/마이 탭만 실제 이동하며, 나머지는 화면이 붙을 때 연결한다.
 */
enum class MainTab(
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val selectedIconRes: Int? = null,
) {
    HOME(R.drawable.ic_tab_home_24, R.string.home_nav_home),
    SAVED(R.drawable.ic_tab_bookmark_24, R.string.home_nav_saved, R.drawable.ic_bookmark_filled_24),
    MY(R.drawable.ic_tab_person_24, R.string.home_nav_my),
}

/**
 * [selectedTab]이 null이면 어떤 탭도 강조하지 않는다(타 유저 프로필처럼 탭 소속이 아닌 화면).
 */
@Composable
fun CourmyBottomBar(
    selectedTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(BottomBarHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTab.entries.forEach { tab ->
                BottomBarTabItem(
                    tab = tab,
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomBarTabItem(
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint =
        if (selected) {
            DesignSystemThemeImpl.designSystemColor.contentAccent
        } else {
            DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2
        }
    Column(
        modifier =
            modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 선택된 탭에 채움 아이콘이 있으면 그걸 사용(예: 저장 탭 → 채운 북마크).
        val iconRes = if (selected) (tab.selectedIconRes ?: tab.iconRes) else tab.iconRes
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(TabIconSize),
        )
        DsText(
            text = stringResource(tab.labelRes),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = tint,
        )
    }
}
