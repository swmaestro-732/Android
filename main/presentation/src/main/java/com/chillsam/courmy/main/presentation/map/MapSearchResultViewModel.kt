package com.chillsam.courmy.main.presentation.map

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetPlaceSavedUseCase
import com.chillsam.courmy.main.domain.map.SearchMapPlacesUseCase
import com.chillsam.courmy.main.domain.map.SuggestMapPlacesUseCase
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCameraMove
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.presentation.search.searchErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSearchResultViewModel
    @Inject
    constructor(
        private val searchPlaces: SearchMapPlacesUseCase,
        private val setPlaceSaved: SetPlaceSavedUseCase,
        private val suggestPlaces: SuggestMapPlacesUseCase,
    ) : MviViewModel<MapSearchResultIntent, MapSearchResultUIState, MapSearchResultReducerEvent>(
            MapSearchResultUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var initialized = false
        private var fallbackAttempted = false
        private var moveId = 0L
        private val savingIds = mutableSetOf<Long>()

        override fun onIntent(intent: MapSearchResultIntent) {
            when (intent) {
                is MapSearchResultIntent.Load -> {
                    loadIfChanged(intent)
                }

                is MapSearchResultIntent.BoundsChanged -> {
                    boundsChanged(intent.bounds)
                }

                is MapSearchResultIntent.SelectCategory -> {
                    loadIfCategoryChanged(intent.category)
                }

                MapSearchResultIntent.CameraMoving -> {
                    loadJob?.cancel()
                    dispatch(MapSearchResultReducerEvent.CameraMoving)
                }

                is MapSearchResultIntent.DestinationReached -> {
                    if (currentState.cameraMove?.id != intent.moveId) return
                    dispatch(MapSearchResultReducerEvent.CameraStopped)
                    load(currentState.query, bounds = intent.bounds)
                }

                MapSearchResultIntent.Retry, MapSearchResultIntent.Refresh -> {
                    load(currentState.query)
                }

                // /places/map은 커서 페이지네이션이 없다.
                MapSearchResultIntent.LoadMore -> {
                    Unit
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

        private fun loadIfCategoryChanged(category: MapCategory) {
            if (category != currentState.category) load(currentState.query, category = category)
        }

        private fun boundsChanged(bounds: MapBounds) {
            if (currentState.cameraMove != null) return
            val wasMoving = currentState.isCameraMoving
            dispatch(MapSearchResultReducerEvent.CameraStopped)
            if (!initialized) {
                dispatch(
                    MapSearchResultReducerEvent.LoadStarted(
                        currentState.query,
                        currentState.latitude,
                        currentState.longitude,
                        false,
                        bounds,
                        currentState.category,
                    ),
                )
            } else if (wasMoving || bounds != currentState.bounds) {
                load(currentState.query, bounds = bounds)
            }
        }

        private fun loadIfChanged(intent: MapSearchResultIntent.Load) {
            if (initialized) return
            initialized = true
            fallbackAttempted = false
            dispatch(
                MapSearchResultReducerEvent.LoadStarted(
                    intent.query,
                    intent.latitude,
                    intent.longitude,
                    false,
                    currentState.bounds,
                    currentState.category,
                ),
            )
            val latitude = intent.targetLatitude?.takeIf { it in -90.0..90.0 }
            val longitude = intent.targetLongitude?.takeIf { it in -180.0..180.0 }
            if (latitude != null && longitude != null) {
                fallbackAttempted = true
                requestMove(latitude, longitude)
            } else if (currentState.bounds != null) {
                load(intent.query)
            }
        }

        private fun requestMove(
            latitude: Double,
            longitude: Double,
        ) {
            dispatch(MapSearchResultReducerEvent.CameraMoveRequested(MapCameraMove(++moveId, latitude, longitude)))
        }

        private fun load(
            query: String,
            append: Boolean = false,
            latitude: Double? = currentState.latitude,
            longitude: Double? = currentState.longitude,
            bounds: MapBounds? = currentState.bounds,
            category: MapCategory = currentState.category,
        ) {
            loadJob?.cancel()
            if (currentState.cameraMove != null) return
            dispatch(MapSearchResultReducerEvent.LoadStarted(query, latitude, longitude, append, bounds, category))
            if (bounds == null) return
            loadJob =
                viewModelScope.launch {
                    runCatching {
                        val result = searchPlaces(query, bounds, category, latitude, longitude)
                        currentCoroutineContext().ensureActive()
                        val noResults = result.places.isEmpty() && result.clusters.isEmpty()
                        if (!fallbackAttempted && query.isNotBlank() && noResults) {
                            val destination = suggestPlaces(query).firstOrNull()
                            currentCoroutineContext().ensureActive()
                            fallbackAttempted = true
                            if (destination != null) {
                                requestMove(
                                    requireNotNull(destination.latitude),
                                    requireNotNull(destination.longitude),
                                )
                                return@launch
                            }
                        } else {
                            fallbackAttempted = true
                        }
                        dispatch(
                            MapSearchResultReducerEvent.Loaded(
                                page = CursorPageVO(items = result.places),
                                clusters = result.clusters,
                                append = false,
                            ),
                        )
                    }.onFailure { error ->
                        if (error is CancellationException) throw error
                        currentCoroutineContext().ensureActive()
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
                            currentCoroutineContext().ensureActive()
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
                is MapSearchResultReducerEvent.CameraMoveRequested -> {
                    state.copy(cameraMove = event.move, isCameraMoving = true, isLoading = true, selectedPlaceId = null)
                }

                MapSearchResultReducerEvent.CameraMoving -> {
                    state.copy(isCameraMoving = true, isLoading = true, errorMessage = null)
                }

                MapSearchResultReducerEvent.CameraStopped -> {
                    state.copy(cameraMove = null, isCameraMoving = false)
                }

                is MapSearchResultReducerEvent.LoadStarted -> {
                    loadingState(state, event)
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
                    state.withSaveToggled(event)
                }

                is MapSearchResultReducerEvent.ActionFailed -> {
                    state.copy(actionErrorMessage = event.message)
                }

                MapSearchResultReducerEvent.ErrorConsumed -> {
                    state.copy(actionErrorMessage = null)
                }
            }

        private fun loadingState(
            state: MapSearchResultUIState,
            event: MapSearchResultReducerEvent.LoadStarted,
        ): MapSearchResultUIState =
            if (event.query == state.query && event.latitude == state.latitude && event.longitude == state.longitude) {
                state.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    bounds = event.bounds,
                    category = event.category,
                    errorMessage = null,
                )
            } else {
                MapSearchResultUIState(
                    query = event.query,
                    latitude = event.latitude,
                    longitude = event.longitude,
                    bounds = event.bounds,
                    category = event.category,
                    sheetMode = state.sheetMode,
                )
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
                clusters = event.clusters.toImmutableList(),
                nextCursor = event.page.nextCursor,
                hasNext = event.page.hasNext && event.page.nextCursor != state.nextCursor,
                selectedPlaceId =
                    state.selectedPlaceId?.takeIf { id -> places.any { it.id == id } },
            )
        }
    }

/** 저장 토글을 목록에 반영한다. 상태만 매만지는 순수 변환이라 클래스 밖에 둔다. */
private fun MapSearchResultUIState.withSaveToggled(
    event: MapSearchResultReducerEvent.SaveToggled,
): MapSearchResultUIState =
    copy(
        places =
            places
                .map { if (it.id == event.placeId) it.copy(saved = event.saved) else it }
                .toImmutableList(),
    )
