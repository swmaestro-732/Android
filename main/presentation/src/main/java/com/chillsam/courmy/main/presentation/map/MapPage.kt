package com.chillsam.courmy.main.presentation.map

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.map.MapSearchResultPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.map.mock.MapMockData

/**
 * 지도 탭(FS-10)의 라우팅 진입점.
 *
 * 지도가 화면 전체를 쓰므로 다른 탭 화면과 달리 상단 여백을 화면(Screen) 안에서 처리한다.
 * 하단 탭바만 여기서 붙인다.
 */
@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onIntent(MapIntent.Load) }

    // 조회 실패는 화면을 비우지 않고 토스트로만 알린다 — 이미 그려 둔 지도는 그대로 쓸 수 있다.
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(MapIntent.ConsumeError)
        }
    }

    val actions =
        remember(viewModel, navigationHelper) {
            MapActions(
                // TODO(SCRUM-지도검색): 검색어 입력 화면이 붙으면 그쪽을 거쳐 결과로 넘긴다.
                onSearchClick = {
                    navigationHelper.navigateByRoute(MapSearchResultPage.route(MapMockData.SAMPLE_QUERY))
                },
                // TODO(SCRUM-지도필터): 복합 필터(FS-10-Filter)는 이번 범위 밖이라 아직 비어 있다.
                onFilterClick = {},
                onCategorySelect = { viewModel.onIntent(MapIntent.SelectCategory(it)) },
                onPlaceSelect = { viewModel.onIntent(MapIntent.SelectPlace(it)) },
                onSortSelect = { viewModel.onIntent(MapIntent.ChangeSortOrder(it)) },
                onListClick = {
                    navigationHelper.navigateByRoute(
                        MapSearchResultPage.route(MapMockData.SAMPLE_QUERY, listMode = true),
                    )
                },
            )
        }

    Column(modifier = modifier.fillMaxSize()) {
        MapScreen(
            uiState = uiState,
            actions = actions,
            modifier = Modifier.weight(1f),
        )
        CourmyBottomBar(
            selectedTab = MainTab.MAP,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)
                    MainTab.MY -> navigationHelper.navigateTo(MyPage)
                    MainTab.MAP -> Unit
                }
            },
        )
    }
}
