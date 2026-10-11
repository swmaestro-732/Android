package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/** 최근 검색어로 남길 최대 개수. 오래된 것부터 밀어낸다. */
private const val MAX_RECENT_KEYWORDS = 10

/** 검색어 입력과 현재 화면의 최근 검색어를 관리한다. */
@HiltViewModel
class SearchViewModel
    @Inject
    constructor() :
    MviViewModel<SearchIntent, SearchUIState, SearchReducerEvent>(SearchUIState.empty) {
        override fun onIntent(intent: SearchIntent) {
            when (intent) {
                SearchIntent.Load -> load()
                is SearchIntent.ChangeQuery -> dispatch(SearchReducerEvent.QueryChanged(intent.query))
                SearchIntent.ClearQuery -> dispatch(SearchReducerEvent.QueryChanged(""))
                is SearchIntent.RemoveRecent -> removeRecent(intent.keyword)
                SearchIntent.ClearAllRecent -> dispatch(SearchReducerEvent.RecentChanged(emptyList()))
                is SearchIntent.Submit -> submit(intent.keyword)
            }
        }

        private fun load() = Unit

        private fun removeRecent(keyword: String) {
            dispatch(SearchReducerEvent.RecentChanged(currentState.recentKeywords - keyword))
        }

        /** 검색한 말을 최근 목록 맨 앞으로 올린다. 이미 있던 말이면 자리만 옮긴다(중복으로 쌓이지 않게). */
        private fun submit(keyword: String) {
            val trimmed = keyword.trim()
            if (trimmed.isEmpty()) return
            val next = (listOf(trimmed) + (currentState.recentKeywords - trimmed)).take(MAX_RECENT_KEYWORDS)
            dispatch(SearchReducerEvent.RecentChanged(next))
        }

        override fun reduce(
            state: SearchUIState,
            event: SearchReducerEvent,
        ): SearchUIState =
            when (event) {
                is SearchReducerEvent.Loaded -> {
                    state.copy(
                        recentKeywords = event.recentKeywords.toImmutableList(),
                        trendingKeywords = event.trendingKeywords.toImmutableList(),
                        trendingBaseTime = event.trendingBaseTime,
                    )
                }

                is SearchReducerEvent.QueryChanged -> {
                    state.copy(query = event.query)
                }

                is SearchReducerEvent.RecentChanged -> {
                    state.copy(recentKeywords = event.keywords.toImmutableList())
                }
            }
    }
