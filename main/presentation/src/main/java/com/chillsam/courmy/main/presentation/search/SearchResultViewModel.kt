package com.chillsam.courmy.main.presentation.search

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.search.SearchCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel
    @Inject
    constructor(
        private val searchCoursesUseCase: SearchCoursesUseCase,
    ) : MviViewModel<SearchResultIntent, SearchResultUIState, SearchResultReducerEvent>(SearchResultUIState.empty) {
        private var loadJob: Job? = null

        override fun onIntent(intent: SearchResultIntent) {
            when (intent) {
                is SearchResultIntent.Load -> {
                    if (intent.query != currentState.query ||
                        currentState.isLoading
                    ) {
                        load(intent.query)
                    }
                }

                SearchResultIntent.Retry -> {
                    load(currentState.query, append = currentState.courses.isNotEmpty())
                }

                SearchResultIntent.LoadMore -> {
                    if (currentState.canLoadMore) {
                        load(currentState.query, append = true)
                    }
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
                    dispatch(SearchResultReducerEvent.LoadStarted(query, append))
                    runCatching { searchCoursesUseCase(query, cursor = cursor) }
                        .onSuccess { page -> dispatch(SearchResultReducerEvent.Loaded(page, append)) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            dispatch(
                                SearchResultReducerEvent.Failed(
                                    error.searchErrorMessage("코스 검색 결과를 불러오지 못했어요. 다시 시도해 주세요."),
                                ),
                            )
                        }
                }
        }

        override fun reduce(
            state: SearchResultUIState,
            event: SearchResultReducerEvent,
        ): SearchResultUIState =
            when (event) {
                is SearchResultReducerEvent.LoadStarted -> {
                    if (event.append) {
                        state.copy(isLoadingMore = true, errorMessage = null)
                    } else {
                        SearchResultUIState(query = event.query)
                    }
                }

                is SearchResultReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        courses =
                            ((if (event.append) state.courses else emptyList()) + event.page.items)
                                .distinctBy {
                                    it.id
                                }.toImmutableList(),
                        nextCursor = event.page.nextCursor,
                        hasNext = event.page.hasNext && event.page.nextCursor != state.nextCursor,
                    )
                }

                is SearchResultReducerEvent.Failed -> {
                    state.copy(isLoading = false, isLoadingMore = false, errorMessage = event.message)
                }
            }
    }
