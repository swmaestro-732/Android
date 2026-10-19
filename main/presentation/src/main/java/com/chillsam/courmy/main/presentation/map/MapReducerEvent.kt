package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapSortOrder

/** 지도 탭 메인 탐색(FS-10)의 내부 상태 변경 이벤트. */
sealed interface MapReducerEvent : ReducerEvent {
    data class BoundsChanged(
        val bounds: MapBounds,
    ) : MapReducerEvent

    data object LoadStarted : MapReducerEvent

    data class Loaded(
        val places: List<MapPlaceVO>,
        val clusters: List<MapClusterVO>,
        val totalCount: Int,
    ) : MapReducerEvent

    data class Failed(
        val message: String,
    ) : MapReducerEvent

    data class CategoryChanged(
        val category: MapCategory,
    ) : MapReducerEvent

    data class PlaceSelected(
        val placeId: Long?,
    ) : MapReducerEvent

    data class SortOrderChanged(
        val order: MapSortOrder,
    ) : MapReducerEvent

    data class SaveToggled(
        val placeId: Long,
        val saved: Boolean,
    ) : MapReducerEvent

    data object ErrorConsumed : MapReducerEvent
}
