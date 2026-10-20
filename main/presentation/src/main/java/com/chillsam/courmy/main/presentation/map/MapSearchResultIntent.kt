package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface MapSearchResultIntent : MviIntent {
    data class Load(
        val query: String,
        val latitude: Double? = null,
        val longitude: Double? = null,
    ) : MapSearchResultIntent

    data object Retry : MapSearchResultIntent

    /** 장소 상세에서 변경한 저장 상태를 첫 페이지부터 다시 읽는다. */
    data object Refresh : MapSearchResultIntent

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
