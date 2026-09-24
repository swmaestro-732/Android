package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.main.entity.map.MapSortOrder

/** 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 사용자 입력. */
sealed interface MapSearchResultIntent : MviIntent {
    data class Load(
        val query: String,
    ) : MapSearchResultIntent

    data object Retry : MapSearchResultIntent

    /** 세분화 칩 탭. 이미 고른 칩을 다시 누르면 null 로 해제한다. */
    data class SelectRefinement(
        val refinement: String?,
    ) : MapSearchResultIntent

    data class SelectPlace(
        val placeId: Long?,
    ) : MapSearchResultIntent

    /** `목록 보기` ↔ 지도 전환. */
    data object ToggleSheetMode : MapSearchResultIntent

    data class SetSheetExpanded(
        val expanded: Boolean,
    ) : MapSearchResultIntent

    data class ChangeSortOrder(
        val order: MapSortOrder,
    ) : MapSearchResultIntent

    data class ToggleSave(
        val placeId: Long,
    ) : MapSearchResultIntent

    data object ConsumeError : MapSearchResultIntent
}
