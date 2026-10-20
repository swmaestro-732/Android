package com.chillsam.courmy.main.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.home.FeedLoading
import com.chillsam.courmy.main.presentation.home.FeedMessage
import com.chillsam.courmy.main.presentation.map.component.MapBottomSheet
import com.chillsam.courmy.main.presentation.map.component.MapCanvas
import com.chillsam.courmy.main.presentation.map.component.MapCircleIconButton
import com.chillsam.courmy.main.presentation.map.component.MapPlaceDetailCard
import com.chillsam.courmy.main.presentation.map.component.MapRankedPlaceRow
import com.chillsam.courmy.main.presentation.map.component.MapSearchQueryBar
import com.chillsam.courmy.main.presentation.map.component.SheetHorizontalPadding
import com.chillsam.courmy.main.presentation.map.mock.MapMockData
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.rememberFusedLocationSource

private val ScreenHorizontalPadding = 16.dp
private val ControlBottomGap = 12.dp

/** 리스트 시트가 화면에서 차지하는 높이 비율. 접힘(FS-10-List) / 펼침(FS-10-ListAlt) 두 단계. */
private const val LIST_SHEET_EXPANDED_RATIO = 0.62f

/**
 * 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 본문.
 *
 * 상단바·지도·컨트롤은 두 시안이 같고 하단 시트만 다르다. 그래서 화면을 하나로 두고
 * [MapSearchResultUIState.sheetMode] 로 시트만 갈아 끼운다.
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapSearchResultScreen(
    uiState: MapSearchResultUIState,
    actions: MapSearchResultActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val isList = uiState.sheetMode == MapSheetMode.LIST
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val listSheetHeight = screenHeight * LIST_SHEET_EXPANDED_RATIO

    // 내 위치 버튼은 지도 탭과 같은 기기 위치 추적(SCRUM-634). 초기 카메라는 검색 결과가
    // 주인공이라 결과 쪽에 두고, 추적은 버튼을 눌렀을 때만 켠다.
    val locationSource = rememberFusedLocationSource()
    var trackingModeName by rememberSaveable { mutableStateOf(LocationTrackingMode.None.name) }
    val trackingMode = remember(trackingModeName) { LocationTrackingMode.valueOf(trackingModeName) }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        MapCanvas(
            places = uiState.places,
            clusters = emptyList(),
            showAllPlaces = true,
            selectedPlaceId = uiState.selectedPlaceId,
            initialPosition = remember { LatLng(MapMockData.DEFAULT_LATITUDE, MapMockData.DEFAULT_LONGITUDE) },
            onPlaceClick = actions.onPlaceSelect,
            onMapClick = { actions.onPlaceSelect(null) },
            contentPadding = PaddingValues(top = 100.dp, bottom = if (isList) listSheetHeight else MarkerSheetInset),
            locationSource = locationSource,
            locationTrackingMode = trackingMode,
            onCameraGesture = {
                if (trackingMode == LocationTrackingMode.Follow) {
                    trackingModeName = LocationTrackingMode.NoFollow.name
                }
            },
        )

        Column(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MapSearchQueryBar(
                query = uiState.query,
                onBackClick = actions.onBackClick,
                onQueryClick = actions.onQueryClick,
                onClearClick = actions.onClearQuery,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp),
            )
        }

        MapControlsRow(
            // 기기 실제 위치로 카메라를 보낸다. 첫 탭이면 권한 요청부터 시작된다.
            onMyLocation = { trackingModeName = LocationTrackingMode.Follow.name },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isList) listSheetHeight else MarkerSheetInset)
                    .padding(horizontal = ScreenHorizontalPadding, vertical = ControlBottomGap),
        )

        if (uiState.places.isEmpty()) {
            MapBottomSheet(modifier = Modifier.align(Alignment.BottomCenter)) {
                if (uiState.isLoading) {
                    FeedLoading()
                } else {
                    FeedMessage(uiState.errorMessage ?: "검색 결과가 없어요", uiState.errorMessage?.let { actions.onRetry })
                }
            }
        } else if (isList) {
            ResultListSheet(
                uiState = uiState,
                actions = actions,
                modifier = Modifier.align(Alignment.BottomCenter).height(listSheetHeight),
            )
        } else {
            SelectedPlaceSheet(
                uiState = uiState,
                actions = actions,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/** 선택 장소 카드 시트가 가리는 대략적인 높이. 지도·컨트롤을 그만큼 위로 밀어 준다. */
private val MarkerSheetInset = 130.dp

/** `목록 보기` 알약과 내 위치 버튼이 같은 줄에 놓인다. 알약은 가운데, 버튼은 오른쪽 끝. */
@Composable
private fun MapControlsRow(
    onMyLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        MapCircleIconButton(
            iconRes = R.drawable.ic_my_location_24,
            contentDescription = "내 위치",
            onClick = onMyLocation,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

/** 고른 장소 카드. 누르면 장소 상세 시트를 연다. */
@Composable
private fun SelectedPlaceSheet(
    uiState: MapSearchResultUIState,
    actions: MapSearchResultActions,
    modifier: Modifier = Modifier,
) {
    val place = uiState.selectedPlace ?: uiState.places.firstOrNull() ?: return
    MapBottomSheet(
        modifier = modifier,
        expanded = false,
        onExpandedChange = actions.onSheetExpandedChange,
    ) {
        MapPlaceDetailCard(
            place = place,
            onClick = { actions.onPlaceDetail(place.id) },
            onSaveClick = { actions.onSaveToggle(place.id) },
            modifier = Modifier.padding(horizontal = SheetHorizontalPadding),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.navigationBarsPadding())
    }
}

/** FS-10-List 시트: 결과 개수 + 정렬 + 순위 리스트. 손잡이를 끌어 높이를 두 단계로 바꾼다. */
@Composable
private fun ResultListSheet(
    uiState: MapSearchResultUIState,
    actions: MapSearchResultActions,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LoadMoreOnScrollEnd(listState, actions.onLoadMore)
    MapBottomSheet(
        modifier = modifier,
        expanded = true,
        onExpandedChange = actions.onSheetExpandedChange,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SheetHorizontalPadding)
                    .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ResultCountLabel(count = "${uiState.places.size}${if (uiState.hasNext) "+" else ""}")
        }
        if (uiState.places.isEmpty()) {
            EmptyResults(loading = uiState.isLoading)
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding =
                    PaddingValues(
                        start = SheetHorizontalPadding,
                        end = SheetHorizontalPadding,
                        bottom = 20.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(uiState.places, key = { _, place -> place.id }) { index, place ->
                    MapRankedPlaceRow(
                        rank = index + 1,
                        place = place,
                        isSelected = place.id == uiState.selectedPlaceId,
                        onClick = { actions.onPlaceDetail(place.id) },
                        onSaveClick = { actions.onSaveToggle(place.id) },
                    )
                }
                if (uiState.isLoadingMore) item { LoadingMoreFooter() }
                if (uiState.errorMessage != null) item { FeedMessage(uiState.errorMessage, actions.onRetry) }
            }
        }
    }
}

/** `검색 결과 24` — 숫자만 흐리게 두어 제목과 개수를 구분한다. */
@Composable
private fun ResultCountLabel(
    count: String,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DsText(
            text = "검색 결과",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = count.toString(),
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel3,
        )
    }
}

@Composable
private fun ColumnScope.EmptyResults(
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = modifier.fillMaxWidth().weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = if (loading) "검색 결과를 불러오는 중이에요" else "검색 결과가 없어요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
        )
    }
}
