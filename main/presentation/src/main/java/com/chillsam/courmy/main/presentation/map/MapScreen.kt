package com.chillsam.courmy.main.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.presentation.home.FeedMessage
import com.chillsam.courmy.main.presentation.map.component.MapBottomSheet
import com.chillsam.courmy.main.presentation.map.component.MapCanvas
import com.chillsam.courmy.main.presentation.map.component.MapChipRow
import com.chillsam.courmy.main.presentation.map.component.MapCircleIconButton
import com.chillsam.courmy.main.presentation.map.component.MapPlaceCarouselCard
import com.chillsam.courmy.main.presentation.map.component.MapSearchEntryBar
import com.chillsam.courmy.main.presentation.map.component.MapSortSelector
import com.chillsam.courmy.main.presentation.map.component.SheetHorizontalPadding
import com.chillsam.courmy.main.presentation.map.mock.MapMockData
import com.naver.maps.geometry.LatLng
import kotlinx.collections.immutable.toImmutableList

/** 화면 가장자리와 지도 위 컨트롤 사이 여백. */
private val ScreenHorizontalPadding = 16.dp

/** 지도 위 컨트롤이 시트에 가리지 않도록 띄우는 높이. */
private val ControlBottomGap = 12.dp

/** 지도가 그리는 저작권 표기·핀이 시트 아래로 숨지 않게 지도에 주는 내부 여백. */
private val MapBottomInset = 200.dp

/**
 * 지도 탭 메인 탐색(FS-10)의 본문.
 *
 * 지도는 화면 전체를 채우고 검색바·칩·컨트롤·시트가 그 위에 떠 있다. 하단 탭바는 화면 밖(Page)이
 * 붙이므로 여기서는 다루지 않는다.
 */
@Composable
fun MapScreen(
    uiState: MapUIState,
    actions: MapActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val categoryLabels = remember { MapCategory.entries.map { it.label() }.toImmutableList() }
    var recenterSignal by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        MapCanvas(
            places = uiState.places,
            clusters = uiState.clusters,
            selectedPlaceId = uiState.selectedPlaceId,
            myLocation = remember { LatLng(MapMockData.DEFAULT_LATITUDE, MapMockData.DEFAULT_LONGITUDE) },
            // 핀도 카드와 같은 2단계 — 비활성 핀은 선택·포커스, 활성 핀 재탭은 상세 시트.
            onPlaceClick = { id ->
                if (id == uiState.selectedPlaceId) actions.onPlaceDetail(id) else actions.onPlaceSelect(id)
            },
            onMapClick = { actions.onPlaceSelect(null) },
            contentPadding = PaddingValues(bottom = MapBottomInset),
            recenterSignal = recenterSignal,
            onBoundsChanged = actions.onBoundsChanged,
        )

        Column(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MapSearchEntryBar(
                placeholder = "장소 검색",
                onClick = actions.onSearchClick,
                onFilterClick = actions.onFilterClick,
                modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp),
            )
            MapChipRow(
                labels = categoryLabels,
                selectedIndex = MapCategory.entries.indexOf(uiState.category),
                onSelect = { index -> actions.onCategorySelect(MapCategory.entries[index]) },
                horizontalPadding = ScreenHorizontalPadding,
            )
        }

        // 컨트롤을 시트와 같은 Column 에 묶는다 — 시트보다 먼저(BottomEnd) 그리면
        // 시트가 위에 덮여 목록보기·내 위치 버튼이 가려져 눌리지 않는다(SCRUM-632).
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Column(
                modifier =
                    Modifier
                        .align(Alignment.End)
                        .padding(end = ScreenHorizontalPadding, bottom = ControlBottomGap),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MapCircleIconButton(
                    iconRes = R.drawable.ic_filter_list_24,
                    contentDescription = "목록으로 보기",
                    onClick = actions.onListClick,
                )
                MapCircleIconButton(
                    iconRes = R.drawable.ic_my_location_24,
                    contentDescription = "내 위치",
                    onClick = { recenterSignal++ },
                )
            }
            NearbySheet(
                uiState = uiState,
                actions = actions,
            )
        }
    }
}

/** FS-10 하단 시트: 결과 요약 + 정렬 + 가로로 넘기는 장소 카드. */
@Composable
private fun NearbySheet(
    uiState: MapUIState,
    actions: MapActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    MapBottomSheet(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SheetHorizontalPadding)
                    .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DsText(
                text = "조건에 맞는 곳 ${uiState.totalCount}곳",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
            MapSortSelector(selected = uiState.sortOrder, onSelect = actions.onSortSelect)
        }
        if (uiState.errorMessage != null) {
            FeedMessage(uiState.errorMessage, actions.onRetry)
        } else if (uiState.places.isEmpty()) {
            EmptyPlaces(loading = uiState.isLoading)
        } else {
            val listState = rememberLazyListState()
            // 핀을 고르면 캐러셀이 그 카드로 따라간다(SCRUM-632) — 상세 시트 대신
            // 하단 시트 안에서 포커스를 옮겨 지도 맥락을 유지한다.
            LaunchedEffect(uiState.selectedPlaceId, uiState.places) {
                val index = uiState.places.indexOfFirst { it.id == uiState.selectedPlaceId }
                if (index >= 0) listState.animateScrollToItem(index)
            }
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = SheetHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.places, key = { it.id }) { place ->
                    val selected = place.id == uiState.selectedPlaceId
                    MapPlaceCarouselCard(
                        place = place,
                        selected = selected,
                        // 비활성 카드는 먼저 지도 이동·핀 활성화만 한다. 상세 시트는
                        // 이미 활성화된 카드를 다시 탭했을 때 연다(SCRUM-632).
                        onClick = {
                            if (selected) actions.onPlaceDetail(place.id) else actions.onPlaceSelect(place.id)
                        },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EmptyPlaces(
    loading: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = modifier.fillMaxWidth().height(120.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = if (loading) "주변 장소를 찾는 중이에요" else "조건에 맞는 장소가 없어요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
        )
    }
}

/** 카테고리 칩에 쓰는 이름. 서버 값이 아니라 화면 문구라 presentation 에 둔다. */
internal fun MapCategory.label(): String = if (this == MapCategory.ALL) "전체" else name.toPlaceCategoryLabel()
