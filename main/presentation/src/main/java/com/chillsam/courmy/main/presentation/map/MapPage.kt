package com.chillsam.courmy.main.presentation.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.domain.search.SearchPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

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
    val session = LocalSessionUiState.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var detailPlaceId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { viewModel.onIntent(MapIntent.Load) }

    val actions =
        remember(viewModel, navigationHelper, uiState.bounds, uiState.myLat, uiState.myLng) {
            MapActions(
                onBoundsChanged = { viewModel.onIntent(MapIntent.BoundsChanged(it)) },
                onRetry = { viewModel.onIntent(MapIntent.Retry) },
                onSearchClick = {
                    val bounds = uiState.bounds
                    navigationHelper.navigateByRoute(
                        SearchPage.route(
                            target = SearchPage.TARGET_PLACE,
                            latitude = bounds?.let { (it.swLat + it.neLat) / 2 } ?: uiState.myLat,
                            longitude = bounds?.let { (it.swLng + it.neLng) / 2 } ?: uiState.myLng,
                        ),
                    )
                },
                onCategorySelect = { viewModel.onIntent(MapIntent.SelectCategory(it)) },
                // 핀 탭은 선택만 — 시트 캐러셀이 해당 카드로 포커스된다(SCRUM-632).
                // 상세 시트는 카드를 탭했을 때만 연다(지도 맥락을 덮지 않기 위해).
                onPlaceSelect = { viewModel.onIntent(MapIntent.SelectPlace(it)) },
                onPlaceDetail = {
                    detailPlaceId = it
                    viewModel.onIntent(MapIntent.SelectPlace(it))
                },
                onSortSelect = { viewModel.onIntent(MapIntent.ChangeSortOrder(it)) },
                onMyLocationChange = { lat, lng ->
                    viewModel.onIntent(MapIntent.MyLocationChanged(lat, lng))
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
                    MainTab.HOME -> {
                        navigationHelper.navigateTo(HomePage)
                    }

                    MainTab.SAVED -> {
                        navigationHelper.navigateTo(SavedPage)
                    }

                    // 비로그인으로 MyPage 에 들어가면 프로필 조회가 401 로 떨어져 오류 화면만 남는다.
                    // 그 화면에는 탭바가 없어 뒤로가기 말고는 빠져나갈 길도 없다.
                    MainTab.MY -> {
                        navigationHelper.navigateTo(if (session.isLoggedIn) MyPage else GuestMyPage)
                    }

                    MainTab.MAP -> {
                        Unit
                    }
                }
            },
        )
    }
    uiState.places.firstOrNull { it.id == detailPlaceId }?.let { place ->
        MapPlaceDetailSheet(
            place = place,
            onDismiss = {
                detailPlaceId = null
                viewModel.onIntent(MapIntent.Retry)
            },
        )
    }
}
