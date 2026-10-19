package com.chillsam.courmy.main.presentation.map

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetPlaceSavedUseCase
import com.chillsam.courmy.main.domain.map.SearchMapPlacesUseCase
import com.chillsam.courmy.main.presentation.search.searchErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSearchResultViewModel
    @Inject
    constructor(
        private val searchPlaces: SearchMapPlacesUseCase,
        private val setPlaceSaved: SetPlaceSavedUseCase,
    ) : MviViewModel<MapSearchResultIntent, MapSearchResultUIState, MapSearchResultReducerEvent>(
            MapSearchResultUIState.empty,
        ) {
        private var loadJob: Job? = null
        private val savingIds = mutableSetOf<Long>()

        override fun onIntent(intent: MapSearchResultIntent) {
            when (intent) {
                is MapSearchResultIntent.Load -> {
                    if (intent.query != currentState.query ||
                        currentState.isLoading
                    ) {
                        load(intent.query)
                    }
                }

                MapSearchResultIntent.Retry -> {
                    load(currentState.query, append = currentState.places.isNotEmpty())
                }

                MapSearchResultIntent.Refresh -> {
                    load(currentState.query)
                }

                MapSearchResultIntent.LoadMore -> {
                    if (currentState.canLoadMore) {
                        load(currentState.query, true)
                    }
                }

                is MapSearchResultIntent.SelectPlace -> {
                    dispatch(MapSearchResultReducerEvent.PlaceSelected(intent.placeId))
                }

                MapSearchResultIntent.ShowList -> {
                    dispatch(MapSearchResultReducerEvent.SheetModeChanged(MapSheetMode.LIST))
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

        private fun load(
            query: String,
            append: Boolean = false,
        ) {
            val cursor = if (append) currentState.nextCursor else null
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(MapSearchResultReducerEvent.LoadStarted(query, append))
                    runCatching { searchPlaces(query, cursor) }
                        .onSuccess { page -> dispatch(MapSearchResultReducerEvent.Loaded(page, append)) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            dispatch(
                                MapSearchResultReducerEvent.Failed(
                                    error.searchErrorMessage("장소 검색 결과를 불러오지 못했어요. 다시 시도해 주세요."),
                                ),
                            )
                        }
                }
        }

        private fun toggleSave(id: Long) {
            val place = currentState.places.firstOrNull { it.id == id } ?: return
            if (!savingIds.add(id)) return
            viewModelScope.launch {
                try {
                    runCatching { setPlaceSaved(id, !place.saved) }
                        .onSuccess { dispatch(MapSearchResultReducerEvent.SaveToggled(id, !place.saved)) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            dispatch(
                                MapSearchResultReducerEvent.ActionFailed(
                                    error.searchErrorMessage("장소 저장을 변경하지 못했어요. 로그인 상태를 확인해 주세요."),
                                ),
                            )
                        }
                } finally {
                    savingIds.remove(id)
                }
            }
        }

        override fun reduce(
            state: MapSearchResultUIState,
            event: MapSearchResultReducerEvent,
        ): MapSearchResultUIState =
            when (event) {
                is MapSearchResultReducerEvent.LoadStarted -> {
                    if (event.append) {
                        state.copy(
                            isLoadingMore = true,
                            errorMessage = null,
                        )
                    } else {
                        MapSearchResultUIState(query = event.query, sheetMode = state.sheetMode)
                    }
                }

                is MapSearchResultReducerEvent.Loaded -> {
                    loadedState(state, event)
                }

                is MapSearchResultReducerEvent.Failed -> {
                    state.copy(isLoading = false, isLoadingMore = false, errorMessage = event.message)
                }

                is MapSearchResultReducerEvent.PlaceSelected -> {
                    state.copy(selectedPlaceId = event.placeId)
                }

                is MapSearchResultReducerEvent.SheetModeChanged -> {
                    state.copy(sheetMode = event.mode, sheetExpanded = false)
                }

                is MapSearchResultReducerEvent.SheetExpandedChanged -> {
                    state.copy(
                        sheetExpanded = event.expanded,
                        sheetMode = if (event.expanded) MapSheetMode.LIST else MapSheetMode.MARKER,
                    )
                }

                is MapSearchResultReducerEvent.SaveToggled -> {
                    state.copy(
                        places =
                            state.places
                                .map {
                                    if (it.id ==
                                        event.placeId
                                    ) {
                                        it.copy(saved = event.saved)
                                    } else {
                                        it
                                    }
                                }.toImmutableList(),
                    )
                }

                is MapSearchResultReducerEvent.ActionFailed -> {
                    state.copy(actionErrorMessage = event.message)
                }

                MapSearchResultReducerEvent.ErrorConsumed -> {
                    state.copy(actionErrorMessage = null)
                }
            }

        private fun loadedState(
            state: MapSearchResultUIState,
            event: MapSearchResultReducerEvent.Loaded,
        ): MapSearchResultUIState {
            val places =
                ((if (event.append) state.places else emptyList()) + event.page.items)
                    .distinctBy {
                        it.id
                    }.toImmutableList()
            return state.copy(
                isLoading = false,
                isLoadingMore = false,
                places = places,
                nextCursor = event.page.nextCursor,
                hasNext = event.page.hasNext && event.page.nextCursor != state.nextCursor,
                selectedPlaceId =
                    state.selectedPlaceId?.takeIf { id -> places.any { it.id == id } }
                        ?: places.firstOrNull()?.id,
            )
        }
    }
