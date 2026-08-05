package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.domain.SearchExternalPlacesUseCase
import com.chillsam.courmy.course.domain.SearchPlacesUseCase
import com.chillsam.courmy.course.entity.CoursePlaceVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PlaceSearchIntent : MviIntent {
    /** 검색어 변경. 입력이 멎은 뒤에 한 번만 서버를 부른다. */
    data class QueryChanged(
        val query: String,
    ) : PlaceSearchIntent

    /**
     * "새로운 위치 추가" 확인 — 다이얼로그에 입력한 이름으로 외부 지도에서 찾는다.
     * 검색창 입력과 별개라 이름을 함께 싣는다.
     */
    data class SearchOnMap(
        val name: String,
    ) : PlaceSearchIntent
}

/** [query] 는 화면 입력값, [results] 는 마지막으로 조회에 성공한 결과다. */
data class PlaceSearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<CoursePlaceVO> = emptyList(),
    val errorMessage: String? = null,
    /** 현재 결과가 외부 지도 검색에서 온 것인지. 화면이 출처를 알려 주는 데 쓴다. */
    val fromMapSearch: Boolean = false,
) : UiState {
    companion object {
        val empty = PlaceSearchUiState()
    }
}

sealed interface PlaceSearchReducerEvent : ReducerEvent {
    data class QueryUpdated(
        val query: String,
    ) : PlaceSearchReducerEvent

    data object Started : PlaceSearchReducerEvent

    data class Loaded(
        val results: List<CoursePlaceVO>,
        val fromMapSearch: Boolean = false,
    ) : PlaceSearchReducerEvent

    data class Failed(
        val message: String,
    ) : PlaceSearchReducerEvent
}

/**
 * 장소 검색(FS-34 "장소 더 담기") ViewModel.
 *
 * 글자마다 요청하지 않도록 [DEBOUNCE_MS] 만큼 입력이 멎기를 기다린 뒤 조회하고,
 * 새 입력이 오면 이전 조회를 취소한다.
 */
@HiltViewModel
class PlaceSearchViewModel
    @Inject
    constructor(
        private val searchPlacesUseCase: SearchPlacesUseCase,
        private val searchExternalPlacesUseCase: SearchExternalPlacesUseCase,
    ) : MviViewModel<PlaceSearchIntent, PlaceSearchUiState, PlaceSearchReducerEvent>(
            PlaceSearchUiState.empty,
        ) {
        private var searchJob: Job? = null

        override fun onIntent(intent: PlaceSearchIntent) {
            when (intent) {
                is PlaceSearchIntent.QueryChanged -> search(intent.query)
                is PlaceSearchIntent.SearchOnMap -> searchOnMap(intent.name)
            }
        }

        override fun reduce(
            state: PlaceSearchUiState,
            event: PlaceSearchReducerEvent,
        ): PlaceSearchUiState =
            when (event) {
                is PlaceSearchReducerEvent.QueryUpdated -> {
                    // 검색어가 바뀌면 이전 결과를 즉시 비워, 다른 키워드의 결과가 남아 보이지 않게 한다.
                    state.copy(
                        query = event.query,
                        results = emptyList(),
                        errorMessage = null,
                        fromMapSearch = false,
                    )
                }

                PlaceSearchReducerEvent.Started -> {
                    state.copy(isSearching = true, errorMessage = null)
                }

                is PlaceSearchReducerEvent.Loaded -> {
                    state.copy(
                        isSearching = false,
                        results = event.results,
                        errorMessage = null,
                        fromMapSearch = event.fromMapSearch,
                    )
                }

                is PlaceSearchReducerEvent.Failed -> {
                    state.copy(isSearching = false, results = emptyList(), errorMessage = event.message)
                }
            }

        /**
         * 외부 지도 검색. 다이얼로그에서 확인을 누른 즉시 부른다
         * (사용자가 명시적으로 요청한 동작이라 debounce 가 오히려 반응을 늦춘다).
         *
         * 결과는 검색창 목록 자리에 그대로 보여 준다 — 담는 방식(선택 → 담기 완료)이 같아
         * 별도 화면을 두면 흐름만 길어진다.
         */
        private fun searchOnMap(name: String) {
            val query = name.trim()
            if (query.isBlank()) return
            searchJob?.cancel()
            searchJob =
                viewModelScope.launch {
                    dispatch(PlaceSearchReducerEvent.Started)
                    runCatching { searchExternalPlacesUseCase(query) }
                        .onSuccess { dispatch(PlaceSearchReducerEvent.Loaded(it, fromMapSearch = true)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "지도 장소 검색 실패: query=$query", e)
                            dispatch(PlaceSearchReducerEvent.Failed("장소를 찾지 못했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        private fun search(query: String) {
            dispatch(PlaceSearchReducerEvent.QueryUpdated(query))
            searchJob?.cancel()
            if (query.isBlank()) return

            searchJob =
                viewModelScope.launch {
                    delay(DEBOUNCE_MS)
                    dispatch(PlaceSearchReducerEvent.Started)
                    runCatching { searchPlacesUseCase(query) }
                        .onSuccess { dispatch(PlaceSearchReducerEvent.Loaded(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "장소 검색 실패: query=$query", e)
                            dispatch(PlaceSearchReducerEvent.Failed("장소를 불러오지 못했어요."))
                        }
                }
        }

        private companion object {
            const val TAG = "PlaceSearch"
            const val DEBOUNCE_MS = 300L
        }
    }
