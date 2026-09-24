package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapSortOrder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 검색 결과 화면 하단 시트의 두 얼굴.
 *
 * Figma 의 FS-10-ResultMap 과 FS-10-List 는 상단·지도가 완전히 같고 시트 내용만 다르다.
 * 그래서 화면을 둘로 나누지 않고 이 값 하나로 전환한다.
 */
enum class MapSheetMode {
    /** 고른 핀 하나를 카드로 보여준다(FS-10-ResultMap). */
    MARKER,

    /** 결과 전체를 순위 리스트로 보여준다(FS-10-List). */
    LIST,
}

/**
 * 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 화면 상태.
 *
 * [sheetExpanded] 는 리스트 시트의 높이 단계다. Figma 의 FS-10-List(접힘)와 FS-10-ListAlt(펼침)가
 * 같은 화면의 두 단계라 별도 화면 대신 boolean 으로 둔다.
 */
data class MapSearchResultUIState(
    val query: String = "",
    val isLoading: Boolean = true,
    val places: ImmutableList<MapPlaceVO> = persistentListOf(),
    val clusters: ImmutableList<MapClusterVO> = persistentListOf(),
    /** 검색어에 딸린 세분화 칩(예: 카페·베이커리·브런치). */
    val refinements: ImmutableList<String> = persistentListOf(),
    val selectedRefinement: String? = null,
    val totalCount: Int = 0,
    val sheetMode: MapSheetMode = MapSheetMode.MARKER,
    val sheetExpanded: Boolean = false,
    val selectedPlaceId: Long? = null,
    val sortOrder: MapSortOrder = MapSortOrder.DISTANCE,
    val errorMessage: String? = null,
) : UiState {
    val selectedPlace: MapPlaceVO?
        get() = places.firstOrNull { it.id == selectedPlaceId }

    companion object {
        val empty: MapSearchResultUIState = MapSearchResultUIState()
    }
}
