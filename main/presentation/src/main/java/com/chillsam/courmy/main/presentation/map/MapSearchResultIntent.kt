package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory

sealed interface MapSearchResultIntent : MviIntent {
    data class Load(
        val query: String,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val targetLatitude: Double? = null,
        val targetLongitude: Double? = null,
    ) : MapSearchResultIntent

    data class BoundsChanged(
        val bounds: MapBounds,
    ) : MapSearchResultIntent

    /** 지도 위 카테고리 칩 선택. 같은 영역·검색어로 그 카테고리만 다시 조회한다. */
    data class SelectCategory(
        val category: MapCategory,
    ) : MapSearchResultIntent

    data object CameraMoving : MapSearchResultIntent

    data class DestinationReached(
        val moveId: Long,
        val bounds: MapBounds,
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
