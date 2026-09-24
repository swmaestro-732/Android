package com.chillsam.courmy.main.presentation.search

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.presentation.search.mock.SearchMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 검색 결과 화면(FS-18).
 *
 * TODO(SCRUM-검색API): 지금은 [SearchMockData] 를 읽어 검색어와 무관하게 같은 결과를 돌려준다.
 * 통합 검색 API 가 붙으면 UseCase 주입으로 바꾸고 검색어를 실제로 넘긴다.
 */
@HiltViewModel
class SearchResultViewModel
    @Inject
    constructor() :
    MviViewModel<SearchResultIntent, SearchResultUIState, SearchResultReducerEvent>(
            SearchResultUIState.empty,
        ) {
        private var loadJob: Job? = null

        override fun onIntent(intent: SearchResultIntent) {
            when (intent) {
                is SearchResultIntent.Load -> {
                    // 같은 검색어로 재진입(구성 변경·복귀)하면 이미 띄운 결과를 그대로 둔다.
                    if (intent.query != currentState.query || currentState.places.isEmpty()) {
                        load(intent.query)
                    }
                    dispatch(SearchResultReducerEvent.TabSelected(intent.tab))
                }

                SearchResultIntent.Retry -> {
                    load(currentState.query)
                }

                is SearchResultIntent.SelectTab -> {
                    dispatch(SearchResultReducerEvent.TabSelected(intent.tab))
                }

                is SearchResultIntent.ToggleSave -> {
                    toggleSave(intent.placeId)
                }

                SearchResultIntent.ConsumeError -> {
                    dispatch(SearchResultReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun load(query: String) {
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(SearchResultReducerEvent.LoadStarted(query))
                    dispatch(
                        SearchResultReducerEvent.Loaded(
                            places = SearchMockData.places,
                            courses = SearchMockData.courses,
                            placeTotalCount = SearchMockData.PLACE_TOTAL_COUNT,
                            courseTotalCount = SearchMockData.COURSE_TOTAL_COUNT,
                        ),
                    )
                }
        }

        private fun toggleSave(placeId: Long) {
            val saved = currentState.places.firstOrNull { it.id == placeId }?.saved ?: return
            dispatch(SearchResultReducerEvent.SaveToggled(placeId = placeId, saved = !saved))
        }

        override fun reduce(
            state: SearchResultUIState,
            event: SearchResultReducerEvent,
        ): SearchResultUIState =
            when (event) {
                is SearchResultReducerEvent.LoadStarted -> {
                    state.copy(query = event.query, isLoading = true, errorMessage = null)
                }

                is SearchResultReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        places = event.places.toImmutableList(),
                        courses = event.courses.toImmutableList(),
                        placeTotalCount = event.placeTotalCount,
                        courseTotalCount = event.courseTotalCount,
                    )
                }

                is SearchResultReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is SearchResultReducerEvent.TabSelected -> {
                    state.copy(selectedTab = event.tab)
                }

                is SearchResultReducerEvent.SaveToggled -> {
                    state.copy(
                        places =
                            state.places
                                .map { place ->
                                    if (place.id == event.placeId) place.copy(saved = event.saved) else place
                                }.toImmutableList(),
                    )
                }

                SearchResultReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }
    }
