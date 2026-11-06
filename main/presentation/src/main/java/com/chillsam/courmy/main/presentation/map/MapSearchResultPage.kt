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
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.domain.telemetry.selectionChange
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.main.domain.search.SearchPage
import com.chillsam.courmy.main.entity.map.MapBounds

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
    bounds: MapBounds? = null,
    targetLatitude: Double? = null,
    targetLongitude: Double? = null,
    modifier: Modifier = Modifier,
    viewModel: MapSearchResultViewModel = hiltViewModel(),
) {
    val telemetry = LocalTelemetry.current
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var detailPlaceId by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(query, listMode, latitude, longitude, targetLatitude, targetLongitude) {
        viewModel.onIntent(MapSearchResultIntent.Load(query, latitude, longitude, targetLatitude, targetLongitude))
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
    val editQuery: (String, String) -> Unit = { value, button ->
        navigationHelper.navigateToBack()
        navigationHelper.navigateByRoute(
            SearchPage.route(
                SearchPage.TARGET_PLACE,
                value,
                latitude,
                longitude,
                uiState.bounds ?: bounds,
            ),
            source =
                NavigationSource(
                    "/map/search",
                    button,
                    trigger =
                        if (button ==
                            "system_back"
                        ) {
                            "system"
                        } else {
                            "button"
                        },
                ),
        )
    }
    BackHandler(enabled = detailPlaceId == null) { editQuery(query, "system_back") }

    val actions =
        remember(viewModel, navigationHelper, telemetry, query, latitude, longitude, uiState.bounds, bounds) {
            MapSearchResultActions(
                onBoundsChanged = { viewModel.onIntent(MapSearchResultIntent.BoundsChanged(it)) },
                onCategorySelect = {
                    telemetry.selectionChange("/map/search", "category", it.name)
                    viewModel.onIntent(MapSearchResultIntent.SelectCategory(it))
                },
                onCameraMoving = { viewModel.onIntent(MapSearchResultIntent.CameraMoving) },
                onDestinationReached = {
                    id,
                    bounds,
                    ->
                    viewModel.onIntent(MapSearchResultIntent.DestinationReached(id, bounds))
                },
                onBackClick = { editQuery(query, "back") },
                onQueryClick = { editQuery(query, "edit_query") },
                onClearQuery = { editQuery("", "clear_query") },
                onPlaceSelect = {
                    telemetry.selectionChange("/map/search", "selected_place", it?.toString() ?: "none")
                    viewModel.onIntent(MapSearchResultIntent.SelectPlace(it))
                },
                onPlaceDetail = {
                    telemetry.buttonClick(
                        "/map/search",
                        "place_detail",
                        mapOf(
                            "place_id" to it.toString(),
                            "destination" to "place_detail_sheet",
                        ),
                    )
                    detailPlaceId = it
                },
                onSaveToggle = {
                    telemetry.buttonClick("/map/search", "bookmark", mapOf("place_id" to it.toString()))
                    viewModel.onIntent(MapSearchResultIntent.ToggleSave(it))
                },
                onRetry = {
                    telemetry.buttonClick("/map/search", "retry")
                    viewModel.onIntent(MapSearchResultIntent.Retry)
                },
                onLoadMore = { viewModel.onIntent(MapSearchResultIntent.LoadMore) },
                onSheetExpandedChange = {
                    telemetry.selectionChange("/map/search", "result_sheet_expanded", it.toString())
                    viewModel.onIntent(MapSearchResultIntent.SetSheetExpanded(it))
                },
            )
        }

    MapSearchResultScreen(
        uiState = uiState,
        initialLatitude = latitude,
        initialLongitude = longitude,
        initialBounds = bounds,
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
