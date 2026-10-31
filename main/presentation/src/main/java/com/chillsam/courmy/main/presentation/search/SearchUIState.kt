package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.search.TrendingKeywordVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** 검색어 입력 화면(FS-17)의 상태. */
data class SearchUIState(
    val query: String = "",
    val recentKeywords: ImmutableList<String> = persistentListOf(),
    val trendingKeywords: ImmutableList<TrendingKeywordVO> = persistentListOf(),
    val trendingBaseTime: String = "",
) : UiState {
    /** 검색을 실행할 수 있는지. 공백만 넣고 눌러도 결과 화면으로 넘어가지 않게 한다. */
    val canSubmit: Boolean
        get() = query.isNotBlank()

    companion object {
        val empty: SearchUIState = SearchUIState()
    }
}
