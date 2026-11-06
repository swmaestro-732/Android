package com.chillsam.courmy.main.presentation.search

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.map.SuggestMapPlacesUseCase
import com.chillsam.courmy.main.domain.search.RecentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 최근 검색어로 남길 최대 개수. 오래된 것부터 밀어낸다. */
private const val MAX_RECENT_KEYWORDS = 10

/** 검색어 입력과 현재 화면의 최근 검색어를 관리한다. */
@HiltViewModel
class SearchViewModel
    @Inject
    constructor(
        private val suggestPlaces: SuggestMapPlacesUseCase,
        private val recentSearchRepository: RecentSearchRepository,
    ) : MviViewModel<SearchIntent, SearchUIState, SearchReducerEvent>(SearchUIState.empty) {
        private var suggestionsJob: Job? = null

        override fun onIntent(intent: SearchIntent) {
            when (intent) {
                is SearchIntent.Load -> {
                    dispatch(SearchReducerEvent.Configured(intent.placeSearch))
                    // 저장해 둔 최근 검색어를 함께 올린다 — 이게 없으면 진입 때마다 빈 화면이다.
                    dispatch(
                        SearchReducerEvent.Loaded(
                            recentKeywords = recentSearchRepository.getAll(),
                            trendingKeywords = currentState.trendingKeywords,
                            trendingBaseTime = currentState.trendingBaseTime,
                        ),
                    )
                }

                is SearchIntent.ChangeQuery -> {
                    changeQuery(intent.query)
                }

                SearchIntent.ClearQuery -> {
                    changeQuery("")
                }

                is SearchIntent.RemoveRecent -> {
                    removeRecent(intent.keyword)
                }

                SearchIntent.ClearAllRecent -> {
                    changeRecent(emptyList())
                }

                is SearchIntent.Submit -> {
                    submit(intent.keyword)
                }
            }
        }

        private fun changeQuery(query: String) {
            suggestionsJob?.cancel()
            dispatch(SearchReducerEvent.QueryChanged(query))
            if (!currentState.placeSearch || query.isBlank()) return
            suggestionsJob =
                viewModelScope.launch {
                    delay(500)
                    runCatching {
                        val places = suggestPlaces(query)
                        currentCoroutineContext().ensureActive()
                        dispatch(SearchReducerEvent.SuggestionsLoaded(places))
                    }.onFailure { error ->
                        if (error is CancellationException) throw error
                        currentCoroutineContext().ensureActive()
                        dispatch(
                            SearchReducerEvent.SuggestionsFailed(
                                error.searchErrorMessage(
                                    "장소 제안을 불러오지 못했어요. 다시 입력해 주세요.",
                                ),
                            ),
                        )
                    }
                }
        }

        private fun removeRecent(keyword: String) {
            changeRecent(currentState.recentKeywords - keyword)
        }

        /** 최근 검색어 변경을 화면 상태와 로컬 저장에 함께 반영한다(다음 진입에도 남게). */
        private fun changeRecent(keywords: List<String>) {
            recentSearchRepository.replaceAll(keywords)
            dispatch(SearchReducerEvent.RecentChanged(keywords))
        }

        /** 검색한 말을 최근 목록 맨 앞으로 올린다. 이미 있던 말이면 자리만 옮긴다(중복으로 쌓이지 않게). */
        private fun submit(keyword: String) {
            val trimmed = keyword.trim()
            if (trimmed.isEmpty()) return
            val next = (listOf(trimmed) + (currentState.recentKeywords - trimmed)).take(MAX_RECENT_KEYWORDS)
            changeRecent(next)
        }

        override fun reduce(
            state: SearchUIState,
            event: SearchReducerEvent,
        ): SearchUIState =
            when (event) {
                is SearchReducerEvent.Configured -> {
                    state.copy(placeSearch = event.placeSearch)
                }

                is SearchReducerEvent.SuggestionsLoaded -> {
                    state.copy(suggestions = event.places.toImmutableList(), suggestionsLoading = false)
                }

                is SearchReducerEvent.SuggestionsFailed -> {
                    state.copy(suggestionsError = event.message, suggestionsLoading = false)
                }

                is SearchReducerEvent.Loaded -> {
                    state.copy(
                        recentKeywords = event.recentKeywords.toImmutableList(),
                        trendingKeywords = event.trendingKeywords.toImmutableList(),
                        trendingBaseTime = event.trendingBaseTime,
                    )
                }

                is SearchReducerEvent.QueryChanged -> {
                    state.copy(
                        query = event.query,
                        suggestions = persistentListOf(),
                        suggestionsError = null,
                        suggestionsLoading =
                            state.placeSearch && event.query.isNotBlank(),
                    )
                }

                is SearchReducerEvent.RecentChanged -> {
                    state.copy(recentKeywords = event.keywords.toImmutableList())
                }
            }
    }
