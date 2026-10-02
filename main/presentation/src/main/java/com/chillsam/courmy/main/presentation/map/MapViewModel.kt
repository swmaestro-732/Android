package com.chillsam.courmy.main.presentation.map

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.map.GetMapPlacesUseCase
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapSortOrder
import com.chillsam.courmy.main.presentation.map.mock.MapMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 지도 탭 메인 탐색(FS-10).
 *
 * 주변 장소는 [GetMapPlacesUseCase](`GET /api/v1/places/map`)로 실 조회한다. 카테고리 칩은 서버
 * 파라미터로 넘어가고(서버에 없는 디저트만 data 레이어가 클라이언트 필터), 정렬은 거리순만 서버가
 * 지원해 저장순·평점순은 여기서 계속 정렬한다.
 *
 * TODO(SCRUM-지도API): 조회 영역이 기본 진입 지점(성수동) 고정이다. 카메라 이동 시 재조회하려면
 * MapIntent 에 카메라 bounds 이벤트를 추가해야 한다.
 */
@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        private val getMapPlacesUseCase: GetMapPlacesUseCase,
    ) : MviViewModel<MapIntent, MapUIState, MapReducerEvent>(MapUIState.empty) {
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
                    runCatching {
                        getMapPlacesUseCase(
                            swLat = MapMockData.DEFAULT_LATITUDE - BOUNDS_DELTA_LAT,
                            swLng = MapMockData.DEFAULT_LONGITUDE - BOUNDS_DELTA_LNG,
                            neLat = MapMockData.DEFAULT_LATITUDE + BOUNDS_DELTA_LAT,
                            neLng = MapMockData.DEFAULT_LONGITUDE + BOUNDS_DELTA_LNG,
                            category = state.category,
                            sortOrder = state.sortOrder,
                            // 위치 권한 연동 전이라 거리 기준점은 기본 진입 지점으로 둔다.
                            userLat = MapMockData.DEFAULT_LATITUDE,
                            userLng = MapMockData.DEFAULT_LONGITUDE,
                        )
                    }.onSuccess { result ->
                        dispatch(
                            MapReducerEvent.Loaded(
                                places = result.places.sortedBy(state.sortOrder),
                                clusters = result.clusters,
                                totalCount = result.totalCount,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                        Log.w(TAG, "지도 장소 로드 실패", e)
                        dispatch(MapReducerEvent.Failed("주변 장소를 불러오지 못했습니다."))
                    }
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

        private companion object {
            const val TAG = "MapViewModel"

            // 카메라 bounds 연동 전까지 기본 진입 지점 주변 약 ±2km 를 조회 범위로 쓴다(파일 상단 TODO).
            const val BOUNDS_DELTA_LAT = 0.018
            const val BOUNDS_DELTA_LNG = 0.023
        }
    }

/**
 * 정렬 기준을 목록에 적용한다. 서버 지도 응답에 저장 수·평점이 없어 저장순·평점순은
 * 저장 여부 → 평점 순으로 근사한다. 거리순은 도보 시간이 있으면(목업 경로) 그 순서로 세우고,
 * 없으면 안정 정렬이라 서버 정렬(sort=DISTANCE) 순서가 그대로 유지된다.
 */
internal fun List<MapPlaceVO>.sortedBy(order: MapSortOrder): List<MapPlaceVO> =
    when (order) {
        MapSortOrder.SAVED -> sortedWith(compareByDescending<MapPlaceVO> { it.saved }.thenByDescending { it.rating })
        MapSortOrder.DISTANCE -> sortedBy { it.walkingMinutes ?: Int.MAX_VALUE }
        MapSortOrder.RATING -> sortedByDescending { it.rating }
    }
