package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface MapSearchResultIntent : MviIntent {
    data class Load(
        val query: String,
    ) : MapSearchResultIntent

    data object Retry : MapSearchResultIntent

    data object LoadMore : MapSearchResultIntent

    data class SelectPlace(
        val placeId: Long?,
    ) : MapSearchResultIntent

    data object ShowList : MapSearchResultIntent

    data class SetSheetExpanded(
        val expanded: Boolean,
    ) : MapSearchResultIntent

    data class ToggleSave(
        val placeId: Long,
    ) : MapSearchResultIntent

    data object ConsumeError : MapSearchResultIntent
}
