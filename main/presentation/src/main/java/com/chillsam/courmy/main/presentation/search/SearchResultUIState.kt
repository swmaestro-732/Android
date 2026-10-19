package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchResultUIState(
    val query: String = "",
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val courses: ImmutableList<SearchCourseVO> = persistentListOf(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    val isBusy: Boolean
        get() = isLoading || isLoadingMore

    val canLoadMore: Boolean
        get() = !isBusy && hasNext && errorMessage == null

    companion object {
        val empty = SearchResultUIState()
    }
}
