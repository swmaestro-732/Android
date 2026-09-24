package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapSortOrder

/** 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 내부 상태 변경 이벤트. */
sealed interface MapSearchResultReducerEvent : ReducerEvent {
    data class LoadStarted(
        val query: String,
    ) : MapSearchResultReducerEvent

    data class Loaded(
        val places: List<MapPlaceVO>,
        val clusters: List<MapClusterVO>,
        val refinements: List<String>,
        val totalCount: Int,
    ) : MapSearchResultReducerEvent

    data class Failed(
        val message: String,
    ) : MapSearchResultReducerEvent

    data class RefinementSelected(
        val refinement: String?,
    ) : MapSearchResultReducerEvent

    data class PlaceSelected(
        val placeId: Long?,
    ) : MapSearchResultReducerEvent

    data class SheetModeChanged(
        val mode: MapSheetMode,
    ) : MapSearchResultReducerEvent

    data class SheetExpandedChanged(
        val expanded: Boolean,
    ) : MapSearchResultReducerEvent

    data class SortOrderChanged(
        val order: MapSortOrder,
    ) : MapSearchResultReducerEvent

    data class SaveToggled(
        val placeId: Long,
        val saved: Boolean,
    ) : MapSearchResultReducerEvent

    data object ErrorConsumed : MapSearchResultReducerEvent
}
