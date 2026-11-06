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
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.chat.ChatPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.map.MapPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.presentation.FeatureFlags

internal val BottomBarHeight = 60.dp
private val TabIconSize = 24.dp

// 기능 토글로 꺼진 탭(예: 채팅)은 하단바에서 제외한다. 값이 const 라 한 번만 계산한다.
private val visibleMainTabs: List<MainTab> =
    MainTab.entries.filter { it != MainTab.CHAT || FeatureFlags.CHAT_ENABLED }

/**
 * 앱 공용 하단 탭바. 홈·지도·채팅·저장·마이 하단 네비게이션 목적지에서 공유한다.
 * 지도는 목적지 화면이 붙기 전까지 탭만 노출하고, route가 생기면 호출부에서 연결한다.
 */
enum class MainTab(
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val selectedIconRes: Int? = null,
) {
    HOME(R.drawable.ic_tab_home_24, R.string.home_nav_home),
    MAP(R.drawable.ic_tab_map_24, R.string.home_nav_map),
    CHAT(R.drawable.ic_tab_chat_24, R.string.home_nav_chat),
    SAVED(R.drawable.ic_tab_bookmark_24, R.string.home_nav_saved),
    MY(R.drawable.ic_tab_person_24, R.string.home_nav_my),
}

/**
 * 하단 탭을 누르면 그 메인 화면으로 이동한다. 현재 탭([from])을 다시 누르면 아무것도 하지 않는다.
 * 각 메인 화면의 [CourmyBottomBar] `onTabSelected` 에서 공유해, 화면마다 반복되던 when 분기를 없앤다.
 */
internal fun NavigationHelper.navigateToMainTab(
    from: MainTab,
    to: MainTab,
) {
    if (to == from) return
    navigateTo(
        when (to) {
            MainTab.HOME -> HomePage
            MainTab.MAP -> MapPage
            MainTab.CHAT -> ChatPage
            MainTab.SAVED -> SavedPage
            MainTab.MY -> MyPage
        },
        source = NavigationSource("/${from.name.lowercase()}", "bottom_tab_${to.name.lowercase()}"),
    )
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
            visibleMainTabs.forEach { tab ->
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
