package com.chillsam.courmy.main.presentation.map

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.presentation.map.mock.MapMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 지도 검색 결과(FS-10-ResultMap / FS-10-List).
 *
 * TODO(SCRUM-지도API): 지금은 [MapMockData] 를 읽는다. 장소 검색 API 가 붙으면 UseCase 주입으로 바꾼다.
 */
@HiltViewModel
class MapSearchResultViewModel
    @Inject
    constructor() :
    MviViewModel<MapSearchResultIntent, MapSearchResultUIState, MapSearchResultReducerEvent>(
            MapSearchResultUIState.empty,
        ) {
        private var loadJob: Job? = null

        override fun onIntent(intent: MapSearchResultIntent) {
            when (intent) {
                is MapSearchResultIntent.Load -> {
                    // 같은 검색어로 재진입(구성 변경·복귀)하면 이미 띄운 결과를 그대로 둔다.
                    if (intent.query == currentState.query && currentState.places.isNotEmpty()) return
                    load(intent.query)
                }

                MapSearchResultIntent.Retry -> {
                    load(currentState.query)
                }

                is MapSearchResultIntent.SelectRefinement -> {
                    dispatch(MapSearchResultReducerEvent.RefinementSelected(intent.refinement))
                    load(currentState.query)
                }

                is MapSearchResultIntent.ChangeSortOrder -> {
                    dispatch(MapSearchResultReducerEvent.SortOrderChanged(intent.order))
                    load(currentState.query)
                }

                is MapSearchResultIntent.SelectPlace -> {
                    // 핀을 고르면 그 장소를 봐야 하므로 시트를 카드 모드로 되돌린다.
                    dispatch(MapSearchResultReducerEvent.PlaceSelected(intent.placeId))
                    if (intent.placeId != null) {
                        dispatch(MapSearchResultReducerEvent.SheetModeChanged(MapSheetMode.MARKER))
                    }
                }

                MapSearchResultIntent.ToggleSheetMode -> {
                    toggleSheetMode()
                }

                is MapSearchResultIntent.SetSheetExpanded -> {
                    dispatch(MapSearchResultReducerEvent.SheetExpandedChanged(intent.expanded))
                }

                is MapSearchResultIntent.ToggleSave -> {
                    toggleSave(intent.placeId)
                }

                MapSearchResultIntent.ConsumeError -> {
                    dispatch(MapSearchResultReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun toggleSheetMode() {
            val next =
                if (currentState.sheetMode == MapSheetMode.LIST) MapSheetMode.MARKER else MapSheetMode.LIST
            dispatch(MapSearchResultReducerEvent.SheetModeChanged(next))
            // 리스트를 닫았다 다시 열면 항상 접힌 단계에서 시작한다(FS-10-List 기본형).
            if (next == MapSheetMode.MARKER) {
                dispatch(MapSearchResultReducerEvent.SheetExpandedChanged(false))
            }
        }

        private fun load(query: String) {
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(MapSearchResultReducerEvent.LoadStarted(query))
                    val state = currentState
                    val refinement = state.selectedRefinement
                    val filtered =
                        MapMockData.searchResultPlaces.filter { place ->
                            refinement == null ||
                                place.category == refinement ||
                                place.subCategory == refinement
                        }
                    dispatch(
                        MapSearchResultReducerEvent.Loaded(
                            places = filtered.sortedBy(state.sortOrder),
                            clusters = MapMockData.searchClusters,
                            refinements = MapMockData.searchRefinements,
                            totalCount = MapMockData.SEARCH_TOTAL_COUNT,
                        ),
                    )
                }
        }

        private fun toggleSave(placeId: Long) {
            val saved = currentState.places.firstOrNull { it.id == placeId }?.saved ?: return
            dispatch(MapSearchResultReducerEvent.SaveToggled(placeId = placeId, saved = !saved))
        }

        override fun reduce(
            state: MapSearchResultUIState,
            event: MapSearchResultReducerEvent,
        ): MapSearchResultUIState =
            when (event) {
                is MapSearchResultReducerEvent.LoadStarted -> {
                    state.copy(query = event.query, isLoading = true, errorMessage = null)
                }

                is MapSearchResultReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        places = event.places.toImmutableList(),
                        clusters = event.clusters.toImmutableList(),
                        refinements = event.refinements.toImmutableList(),
                        totalCount = event.totalCount,
                        // 첫 진입에는 1위 장소를 미리 골라 둔다 — 시안의 시트가 카드 상태로 시작한다.
                        selectedPlaceId =
                            state.selectedPlaceId?.takeIf { id -> event.places.any { it.id == id } }
                                ?: event.places.firstOrNull()?.id,
                    )
                }

                is MapSearchResultReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is MapSearchResultReducerEvent.RefinementSelected -> {
                    state.copy(selectedRefinement = event.refinement)
                }

                is MapSearchResultReducerEvent.PlaceSelected -> {
                    state.copy(selectedPlaceId = event.placeId)
                }

                is MapSearchResultReducerEvent.SheetModeChanged -> {
                    state.copy(sheetMode = event.mode)
                }

                is MapSearchResultReducerEvent.SheetExpandedChanged -> {
                    state.copy(sheetExpanded = event.expanded)
                }

                is MapSearchResultReducerEvent.SortOrderChanged -> {
                    state.copy(sortOrder = event.order)
                }

                is MapSearchResultReducerEvent.SaveToggled -> {
                    state.copy(
                        places =
                            state.places
                                .map { place ->
                                    if (place.id == event.placeId) place.copy(saved = event.saved) else place
                                }.toImmutableList(),
                    )
                }

                MapSearchResultReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }
    }
