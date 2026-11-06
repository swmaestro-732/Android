package com.chillsam.courmy.main.presentation.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.main.domain.chat.ChatRoomPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.map.MapPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 채팅 탭(하단 네비게이션 가운데)의 라우팅 진입점 = 채팅홈(대화 목록).
 *
 * 목록에서 대화를 누르면 [ChatRoomPage] 방으로 들어간다. 탭 루트라 하단 탭바는 여기서 붙인다.
 */
@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    viewModel: ChatHomeViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onIntent(ChatHomeIntent.Load) }

    Column(modifier = modifier.fillMaxSize()) {
        ChatHomeScreen(
            uiState = uiState,
            onTabSelect = { tab -> viewModel.onIntent(ChatHomeIntent.SelectTab(tab)) },
            onRoomEnter = { roomId ->
                navigationHelper.navigateByRoute(ChatRoomPage.route(roomId))
            },
            modifier = Modifier.weight(1f),
        )
        CourmyBottomBar(
            selectedTab = MainTab.CHAT,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)

                    MainTab.MAP -> navigationHelper.navigateTo(MapPage)

                    MainTab.CHAT -> Unit

                    MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)

                    // 비로그인으로 마이에 들어가면 프로필 조회가 401 로 떨어지므로 게스트 마이로 보낸다.
                    MainTab.MY -> navigationHelper.navigateTo(if (session.isLoggedIn) MyPage else GuestMyPage)
                }
            },
        )
    }
}
