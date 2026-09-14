package com.chillsam.courmy.main.presentation.map

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.map.component.MapBottomSheet
import com.chillsam.courmy.main.presentation.map.component.MapCanvas
import com.chillsam.courmy.main.presentation.map.component.MapChipRow
import com.chillsam.courmy.main.presentation.map.component.MapCircleIconButton
import com.chillsam.courmy.main.presentation.map.component.MapPillButton
import com.chillsam.courmy.main.presentation.map.component.MapPlaceDetailCard
import com.chillsam.courmy.main.presentation.map.component.MapRankedPlaceRow
import com.chillsam.courmy.main.presentation.map.component.MapSearchQueryBar
import com.chillsam.courmy.main.presentation.map.component.MapSortSelector
import com.chillsam.courmy.main.presentation.map.component.SheetHorizontalPadding
import com.chillsam.courmy.main.presentation.map.mock.MapMockData
import com.naver.maps.geometry.LatLng

private val ScreenHorizontalPadding = 16.dp
private val ControlBottomGap = 12.dp

/** 리스트 시트가 화면에서 차지하는 높이 비율. 접힘(FS-10-List) / 펼침(FS-10-ListAlt) 두 단계. */
private const val LIST_SHEET_COLLAPSED_RATIO = 0.45f
private const val LIST_SHEET_EXPANDED_RATIO = 0.62f

/**
 * 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 본문.
 *
 * 상단바·지도·컨트롤은 두 시안이 같고 하단 시트만 다르다. 그래서 화면을 하나로 두고
 * [MapSearchResultUIState.sheetMode] 로 시트만 갈아 끼운다.
 */
@Composable
fun MapSearchResultScreen(
    uiState: MapSearchResultUIState,
    actions: MapSearchResultActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var recenterSignal by remember { mutableIntStateOf(0) }
    val isList = uiState.sheetMode == MapSheetMode.LIST
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val listSheetHeight by animateDpAsState(
        targetValue =
            screenHeight *
                if (uiState.sheetExpanded) LIST_SHEET_EXPANDED_RATIO else LIST_SHEET_COLLAPSED_RATIO,
        label = "listSheetHeight",
    )

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        MapCanvas(
            places = uiState.places,
            clusters = uiState.clusters,
            selectedPlaceId = uiState.selectedPlaceId,
            myLocation = remember { LatLng(MapMockData.DEFAULT_LATITUDE, MapMockData.DEFAULT_LONGITUDE) },
            onPlaceClick = actions.onPlaceSelect,
            onMapClick = { actions.onPlaceSelect(null) },
            contentPadding = PaddingValues(bottom = if (isList) listSheetHeight else MarkerSheetInset),
            recenterSignal = recenterSignal,
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
            // 리스트를 펼치면 지도가 거의 안 보이므로 세분화 칩도 같이 접어 시트에 자리를 내준다.
            if (!uiState.sheetExpanded) {
                MapChipRow(
                    labels = uiState.refinements,
                    selectedIndex = uiState.refinements.indexOf(uiState.selectedRefinement),
                    onSelect = { index ->
                        val label = uiState.refinements[index]
                        actions.onRefinementSelect(label.takeIf { it != uiState.selectedRefinement })
                    },
                    horizontalPadding = ScreenHorizontalPadding,
                )
            }
        }

        MapControlsRow(
            isList = isList,
            onToggleSheetMode = actions.onToggleSheetMode,
            onRecenter = { recenterSignal++ },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isList) listSheetHeight else MarkerSheetInset)
                    .padding(horizontal = ScreenHorizontalPadding, vertical = ControlBottomGap),
        )

        if (isList) {
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
private val MarkerSheetInset = 190.dp

/** `목록 보기` 알약과 내 위치 버튼이 같은 줄에 놓인다. 알약은 가운데, 버튼은 오른쪽 끝. */
@Composable
private fun MapControlsRow(
    isList: Boolean,
    onToggleSheetMode: () -> Unit,
    onRecenter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        // 리스트가 떠 있는 동안에는 `지도 보기` 로 뒤집어 되돌아갈 길을 남긴다.
        MapPillButton(
            text = if (isList) "지도 보기" else "목록 보기",
            iconRes = if (isList) R.drawable.ic_location_24 else R.drawable.ic_filter_list_24,
            onClick = onToggleSheetMode,
        )
        MapCircleIconButton(
            iconRes = R.drawable.ic_my_location_24,
            contentDescription = "내 위치",
            onClick = onRecenter,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

/** FS-10-ResultMap 시트: 고른 장소 카드 + `코스에 추가`. */
@Composable
private fun SelectedPlaceSheet(
    uiState: MapSearchResultUIState,
    actions: MapSearchResultActions,
    modifier: Modifier = Modifier,
) {
    val place = uiState.selectedPlace ?: return
    MapBottomSheet(modifier = modifier) {
        MapPlaceDetailCard(
            place = place,
            onClick = { actions.onPlaceSelect(place.id) },
            onSaveClick = { actions.onSaveToggle(place.id) },
            modifier = Modifier.padding(horizontal = SheetHorizontalPadding),
        )
        Spacer(modifier = Modifier.height(16.dp))
        DsButton(
            text = "+ 코스에 추가",
            onClick = { actions.onAddToCourse(place.id) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = SheetHorizontalPadding),
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
    val color = DesignSystemThemeImpl.designSystemColor
    MapBottomSheet(
        modifier = modifier,
        expanded = uiState.sheetExpanded,
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
            ResultCountLabel(count = uiState.totalCount)
            MapSortSelector(selected = uiState.sortOrder, onSelect = actions.onSortSelect)
        }
        if (uiState.places.isEmpty()) {
            EmptyResults(loading = uiState.isLoading)
        } else {
            LazyColumn(
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
                        onClick = { actions.onPlaceSelect(place.id) },
                        onSaveClick = { actions.onSaveToggle(place.id) },
                    )
                }
            }
        }
    }
}

/** `검색 결과 24` — 숫자만 흐리게 두어 제목과 개수를 구분한다. */
@Composable
private fun ResultCountLabel(
    count: Int,
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
