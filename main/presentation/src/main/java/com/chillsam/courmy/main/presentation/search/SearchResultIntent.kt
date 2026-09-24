package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 검색 결과 화면(FS-18)의 사용자 입력. */
sealed interface SearchResultIntent : MviIntent {
    data class Load(
        val query: String,
        val tab: SearchResultTab,
    ) : SearchResultIntent

    data object Retry : SearchResultIntent

    data class SelectTab(
        val tab: SearchResultTab,
    ) : SearchResultIntent

    data class ToggleSave(
        val placeId: Long,
    ) : SearchResultIntent

    data object ConsumeError : SearchResultIntent
}
