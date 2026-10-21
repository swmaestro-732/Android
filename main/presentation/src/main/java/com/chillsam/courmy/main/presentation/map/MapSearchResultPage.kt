package com.chillsam.courmy.main.presentation.map

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.main.domain.search.SearchPage

/**
 * 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 라우팅 진입점.
 *
 * 하단 탭 소속이 아니라(검색에서 파고든 화면) 탭바를 붙이지 않는다.
 */
@Composable
fun MapSearchResultPage(
    query: String,
    listMode: Boolean,
    latitude: Double? = null,
    longitude: Double? = null,
    modifier: Modifier = Modifier,
    viewModel: MapSearchResultViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var detailPlaceId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(query, listMode, latitude, longitude) {
        viewModel.onIntent(MapSearchResultIntent.Load(query, latitude, longitude))
        // 지도 탭의 목록 버튼으로 들어오면 리스트가 펼쳐진 채로 시작한다.
        if (listMode) viewModel.onIntent(MapSearchResultIntent.ShowList)
    }

    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(MapSearchResultIntent.ConsumeError)
        }
    }

    // 입력 화면은 제출 시 교체되므로, 뒤로가기에서도 검색 조건을 복원한다.
    val editQuery: (String) -> Unit = { value ->
        navigationHelper.navigateToBack()
        navigationHelper.navigateByRoute(SearchPage.route(SearchPage.TARGET_PLACE, value, latitude, longitude))
    }
    BackHandler(enabled = detailPlaceId == null) { editQuery(query) }

    val actions =
        remember(viewModel, navigationHelper, query, latitude, longitude) {
            MapSearchResultActions(
                onBackClick = { editQuery(query) },
                onQueryClick = { editQuery(query) },
                onClearQuery = { editQuery("") },
                onPlaceSelect = {
                    viewModel.onIntent(MapSearchResultIntent.SelectPlace(it))
                },
                onPlaceDetail = { detailPlaceId = it },
                onSaveToggle = { viewModel.onIntent(MapSearchResultIntent.ToggleSave(it)) },
                onRetry = { viewModel.onIntent(MapSearchResultIntent.Retry) },
                onLoadMore = { viewModel.onIntent(MapSearchResultIntent.LoadMore) },
                onSheetExpandedChange = {
                    viewModel.onIntent(MapSearchResultIntent.SetSheetExpanded(it))
                },
            )
        }

    MapSearchResultScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier.fillMaxSize(),
    )
    uiState.places.firstOrNull { it.id == detailPlaceId }?.let { place ->
        MapPlaceDetailSheet(
            place = place,
            onDismiss = {
                detailPlaceId = null
                viewModel.onIntent(MapSearchResultIntent.Refresh)
            },
        )
    }
}
