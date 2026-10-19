package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class MapSheetMode { MARKER, LIST }

data class MapSearchResultUIState(
    val query: String = "",
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val places: ImmutableList<MapPlaceVO> = persistentListOf(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val sheetMode: MapSheetMode = MapSheetMode.MARKER,
    val sheetExpanded: Boolean = false,
    val selectedPlaceId: Long? = null,
    val errorMessage: String? = null,
    val actionErrorMessage: String? = null,
) : UiState {
    val isBusy: Boolean
        get() = isLoading || isLoadingMore

    val canLoadMore: Boolean
        get() = !isBusy && hasNext && errorMessage == null

    val selectedPlace: MapPlaceVO?
        get() = places.firstOrNull { it.id == selectedPlaceId }

    companion object {
        val empty = MapSearchResultUIState()
    }
}
