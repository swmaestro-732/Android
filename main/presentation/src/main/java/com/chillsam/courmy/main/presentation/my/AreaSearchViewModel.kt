package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.area.SearchAreasUseCase
import com.chillsam.courmy.main.entity.area.AreaVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 글자를 칠 때마다 부르지 않도록 입력이 멎기를 기다리는 시간(ms). */
private const val SEARCH_DEBOUNCE_MS = 250L

sealed interface AreaSearchIntent : MviIntent {
    /** 검색어 입력. 잠시 멎으면 자동으로 검색한다. */
    data class QueryChanged(
        val keyword: String,
    ) : AreaSearchIntent
}

data class AreaSearchUiState(
    val keyword: String = "",
    val results: List<AreaVO> = emptyList(),
    val isSearching: Boolean = false,
    /** 검색 실패를 빈 결과와 구분한다. */
    val searchFailed: Boolean = false,
) : UiState {
    companion object {
        val empty = AreaSearchUiState()
    }
}

sealed interface AreaSearchReducerEvent : ReducerEvent {
    data class QueryChanged(
        val keyword: String,
    ) : AreaSearchReducerEvent

    data object Started : AreaSearchReducerEvent

    data class Loaded(
        val results: List<AreaVO>,
        val searchFailed: Boolean,
    ) : AreaSearchReducerEvent
}

/** 관심 지역 검색. 서버가 반환한 코드가 있는 실제 지역만 선택할 수 있다. */
@HiltViewModel
class AreaSearchViewModel
    @Inject
    constructor(
        private val searchAreasUseCase: SearchAreasUseCase,
    ) : MviViewModel<AreaSearchIntent, AreaSearchUiState, AreaSearchReducerEvent>(AreaSearchUiState.empty) {
        private var job: Job? = null

        override fun onIntent(intent: AreaSearchIntent) {
            when (intent) {
                is AreaSearchIntent.QueryChanged -> search(intent.keyword)
            }
        }

        override fun reduce(
            state: AreaSearchUiState,
            event: AreaSearchReducerEvent,
        ): AreaSearchUiState =
            when (event) {
                is AreaSearchReducerEvent.QueryChanged -> {
                    state.copy(keyword = event.keyword, results = emptyList(), searchFailed = false)
                }

                AreaSearchReducerEvent.Started -> {
                    state.copy(isSearching = true, results = emptyList(), searchFailed = false)
                }

                is AreaSearchReducerEvent.Loaded -> {
                    state.copy(
                        results = event.results.filter { it.code.isNotBlank() },
                        isSearching = false,
                        searchFailed = event.searchFailed,
                    )
                }
            }

        private fun search(keyword: String) {
            dispatch(AreaSearchReducerEvent.QueryChanged(keyword))
            job?.cancel()
            if (keyword.isBlank()) {
                dispatch(AreaSearchReducerEvent.Loaded(emptyList(), searchFailed = false))
                return
            }
            dispatch(AreaSearchReducerEvent.Started)
            job =
                viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    runCatching { searchAreasUseCase(keyword) }
                        .onSuccess { dispatch(AreaSearchReducerEvent.Loaded(it, searchFailed = false)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "지역 검색 실패: ${e.message}", e)
                            dispatch(AreaSearchReducerEvent.Loaded(emptyList(), searchFailed = true))
                        }
                }
        }

        private companion object {
            const val TAG = "AreaSearch"
        }
    }
