package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCameraMove
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO

sealed interface MapSearchResultReducerEvent : ReducerEvent {
    data class CameraMoveRequested(
        val move: MapCameraMove,
    ) : MapSearchResultReducerEvent

    data object CameraMoving : MapSearchResultReducerEvent

    data object CameraStopped : MapSearchResultReducerEvent

    data class LoadStarted(
        val query: String,
        val latitude: Double?,
        val longitude: Double?,
        val append: Boolean,
        val bounds: MapBounds? = null,
        val category: MapCategory = MapCategory.ALL,
    ) : MapSearchResultReducerEvent

    data class Loaded(
        val page: CursorPageVO<MapPlaceVO>,
        val clusters: List<MapClusterVO> = emptyList(),
        val append: Boolean,
    ) : MapSearchResultReducerEvent

    data class Failed(
        val message: String,
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

    data class SaveToggled(
        val placeId: Long,
        val saved: Boolean,
    ) : MapSearchResultReducerEvent

    data class ActionFailed(
        val message: String,
    ) : MapSearchResultReducerEvent

    data object ErrorConsumed : MapSearchResultReducerEvent
}
