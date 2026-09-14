package com.chillsam.courmy.main.presentation.map

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapSortOrder
import com.chillsam.courmy.main.presentation.map.mock.MapMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 지도 탭 메인 탐색(FS-10).
 *
 * TODO(SCRUM-지도API): 지금은 [MapMockData] 를 읽는다. 주변 장소 API 가 붙으면 UseCase 주입으로 바꾼다.
 * 카테고리·정렬은 그때 서버 파라미터로 넘어가므로, 화면이 거르지 않고 여기서만 처리하도록 모아 뒀다.
 */
@HiltViewModel
class MapViewModel
    @Inject
    constructor() :
    MviViewModel<MapIntent, MapUIState, MapReducerEvent>(MapUIState.empty) {
        private var loadJob: Job? = null

        override fun onIntent(intent: MapIntent) {
            when (intent) {
                MapIntent.Load -> {
                    if (currentState.places.isEmpty()) load()
                }

                MapIntent.Retry -> {
                    load()
                }

                is MapIntent.SelectCategory -> {
                    dispatch(MapReducerEvent.CategoryChanged(intent.category))
                    load()
                }

                is MapIntent.ChangeSortOrder -> {
                    dispatch(MapReducerEvent.SortOrderChanged(intent.order))
                    load()
                }

                is MapIntent.SelectPlace -> {
                    dispatch(MapReducerEvent.PlaceSelected(intent.placeId))
                }

                is MapIntent.ToggleSave -> {
                    toggleSave(intent.placeId)
                }

                MapIntent.ConsumeError -> {
                    dispatch(MapReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun load() {
            // 카테고리를 연달아 누르면 앞선 조회는 의미가 없다. 항상 마지막 것만 살린다.
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(MapReducerEvent.LoadStarted)
                    val state = currentState
                    val filtered = MapMockData.filterByCategory(MapMockData.nearbyPlaces, state.category)
                    dispatch(
                        MapReducerEvent.Loaded(
                            places = filtered.sortedBy(state.sortOrder),
                            clusters = MapMockData.nearbyClusters,
                            totalCount = MapMockData.NEARBY_TOTAL_COUNT,
                        ),
                    )
                }
        }

        private fun toggleSave(placeId: Long) {
            val saved = currentState.places.firstOrNull { it.id == placeId }?.saved ?: return
            dispatch(MapReducerEvent.SaveToggled(placeId = placeId, saved = !saved))
        }

        override fun reduce(
            state: MapUIState,
            event: MapReducerEvent,
        ): MapUIState =
            when (event) {
                MapReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is MapReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        places = event.places.toImmutableList(),
                        clusters = event.clusters.toImmutableList(),
                        totalCount = event.totalCount,
                        // 걸러낸 결과에 없는 장소가 선택된 채로 남으면 말풍선만 떠 있고 핀은 사라진다.
                        selectedPlaceId = state.selectedPlaceId?.takeIf { id -> event.places.any { it.id == id } },
                    )
                }

                is MapReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is MapReducerEvent.CategoryChanged -> {
                    state.copy(category = event.category)
                }

                is MapReducerEvent.SortOrderChanged -> {
                    state.copy(sortOrder = event.order)
                }

                is MapReducerEvent.PlaceSelected -> {
                    state.copy(selectedPlaceId = event.placeId)
                }

                is MapReducerEvent.SaveToggled -> {
                    state.copy(
                        places =
                            state.places
                                .map { place ->
                                    if (place.id == event.placeId) place.copy(saved = event.saved) else place
                                }.toImmutableList(),
                    )
                }

                MapReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }
    }

/** 정렬 기준을 목록에 적용한다. 저장순은 mock 에 저장 수가 없어 저장 여부 → 평점 순으로 근사한다. */
internal fun List<MapPlaceVO>.sortedBy(order: MapSortOrder): List<MapPlaceVO> =
    when (order) {
        MapSortOrder.SAVED -> sortedWith(compareByDescending<MapPlaceVO> { it.saved }.thenByDescending { it.rating })
        MapSortOrder.DISTANCE -> sortedBy { it.walkingMinutes ?: Int.MAX_VALUE }
        MapSortOrder.RATING -> sortedByDescending { it.rating }
    }
