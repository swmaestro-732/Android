package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.search.TrendingKeywordVO

/** 검색어 입력 화면(FS-17)의 내부 상태 변경 이벤트. */
sealed interface SearchReducerEvent : ReducerEvent {
    data class Loaded(
        val recentKeywords: List<String>,
        val trendingKeywords: List<TrendingKeywordVO>,
        val trendingBaseTime: String,
    ) : SearchReducerEvent

    data class QueryChanged(
        val query: String,
    ) : SearchReducerEvent

    data class RecentChanged(
        val keywords: List<String>,
    ) : SearchReducerEvent
}
