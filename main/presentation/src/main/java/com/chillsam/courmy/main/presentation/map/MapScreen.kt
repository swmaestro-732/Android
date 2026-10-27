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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.presentation.component.hasLocationPermission
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
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.rememberFusedLocationSource
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
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    uiState: MapUIState,
    actions: MapActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val categoryLabels = remember { MapCategory.entries.map { it.label() }.toImmutableList() }

    // 현위치 추적(SCRUM-634). 권한 요청·위치 구독은 FusedLocationSource 가 담당하고,
    // 버튼은 추적 모드만 켠다. 지도를 끌면 카메라 추적(Follow)만 풀고 오버레이는 유지한다(NoFollow).
    // 권한이 이미 있으면 첫 진입부터 기기 위치에서 시작한다. 없으면 기본 좌표(성수)에 두고,
    // 첫 진입에 권한 다이얼로그부터 들이밀지 않는다 — 요청은 내 위치 버튼을 눌렀을 때만.
    val context = LocalContext.current
    val locationSource = rememberFusedLocationSource()
    var trackingModeName by rememberSaveable {
        mutableStateOf(
            if (context.hasLocationPermission()) LocationTrackingMode.Follow.name else LocationTrackingMode.None.name,
        )
    }
    val trackingMode = remember(trackingModeName) { LocationTrackingMode.valueOf(trackingModeName) }

    // 첫 진입은 지도 전체 화면이다(사용자 결정, 2026-10-19). 시트는 핀을 고르거나
    // 카테고리로 거르기 시작했을 때만 띄운다.
    val sheetVisible = uiState.selectedPlaceId != null || uiState.category != MapCategory.ALL

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        MapCanvas(
            places = uiState.markerPlaces,
            clusters = uiState.clusters,
            selectedPlaceId = uiState.selectedPlaceId,
            initialPosition = remember { LatLng(MapMockData.DEFAULT_LATITUDE, MapMockData.DEFAULT_LONGITUDE) },
            // 핀도 카드와 같은 2단계 — 비활성 핀은 선택·포커스, 활성 핀 재탭은 상세 시트.
            onPlaceClick = { id ->
                if (id == uiState.selectedPlaceId) actions.onPlaceDetail(id) else actions.onPlaceSelect(id)
            },
            onMapClick = { actions.onPlaceSelect(null) },
            // 시트가 없을 때도 여백을 고정하면 네이버 로고·저작권 표기가 허공에 뜬다.
            contentPadding = PaddingValues(bottom = if (sheetVisible) MapBottomInset else 0.dp),
            onBoundsChanged = actions.onBoundsChanged,
            locationSource = locationSource,
            locationTrackingMode = trackingMode,
            onLocationChange = { actions.onMyLocationChange(it.latitude, it.longitude) },
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
        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            // 목록보기 버튼은 사용자 요청으로 제거(2026-10-19) — 시트 캐러셀이 목록 역할을 한다.
            MapCircleIconButton(
                iconRes = R.drawable.ic_my_location_24,
                contentDescription = "내 위치",
                // 기기 실제 위치로 카메라를 보낸다. 첫 탭이면 권한 요청부터 시작된다.
                onClick = { trackingModeName = LocationTrackingMode.Follow.name },
                modifier =
                    Modifier
                        .align(Alignment.End)
                        .padding(end = ScreenHorizontalPadding, bottom = ControlBottomGap),
            )
            if (sheetVisible) {
                NearbySheet(
                    uiState = uiState,
                    actions = actions,
                )
            } else if (uiState.errorMessage != null) {
                // 첫 진입은 장소 시트를 숨겨도 조회 실패와 재시도는 보여 준다.
                // 오류 표시 때문에 지도 영역을 바꾸면 불필요한 재조회가 생기므로 padding은 유지한다.
                MapBottomSheet {
                    FeedMessage(uiState.errorMessage, actions.onRetry)
                }
            }
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
                // 검색·필터 전에도 뜨는 시트라 "조건" 대신 지금 화면에 보이는 장소임을 말한다(사용자 결정).
                text = "보이는 장소 ${uiState.totalCount}곳",
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
