package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class MapSheetMode { MARKER, LIST }

data class MapSearchResultUIState(
    val query: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val places: ImmutableList<MapPlaceVO> = persistentListOf(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    // 검색하면 결과 리스트가 바로 펼쳐진 상태로 보이도록 기본을 LIST 로 둔다(손잡이로 접으면 MARKER).
    val sheetMode: MapSheetMode = MapSheetMode.LIST,
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
