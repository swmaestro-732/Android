package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface SearchResultIntent : MviIntent {
    data class Load(
        val query: String,
    ) : SearchResultIntent

    data object Retry : SearchResultIntent

    data object LoadMore : SearchResultIntent
}
