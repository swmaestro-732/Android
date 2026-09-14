package com.chillsam.courmy.main.presentation.map

import androidx.compose.runtime.Immutable
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapSortOrder

/**
 * 지도 탭 화면이 상위로 올려 보내는 동작 묶음(FS-10).
 *
 * 내 위치로 돌아가기처럼 화면 안에서 끝나는 동작은 여기 없다 — 카메라를 어디에 두느냐는 앱 상태가
 * 아니라 그리기의 문제라 Screen 이 자기 안에서 처리한다.
 */
@Immutable
data class MapActions(
    val onSearchClick: () -> Unit,
    val onFilterClick: () -> Unit,
    val onCategorySelect: (MapCategory) -> Unit,
    val onPlaceSelect: (Long?) -> Unit,
    val onSortSelect: (MapSortOrder) -> Unit,
    val onListClick: () -> Unit,
)

/** 검색 결과 화면이 상위로 올려 보내는 동작 묶음(FS-10-ResultMap / FS-10-List). */
@Immutable
data class MapSearchResultActions(
    val onBackClick: () -> Unit,
    val onQueryClick: () -> Unit,
    val onClearQuery: () -> Unit,
    val onRefinementSelect: (String?) -> Unit,
    val onPlaceSelect: (Long?) -> Unit,
    val onSaveToggle: (Long) -> Unit,
    val onSortSelect: (MapSortOrder) -> Unit,
    val onToggleSheetMode: () -> Unit,
    val onSheetExpandedChange: (Boolean) -> Unit,
    val onAddToCourse: (Long) -> Unit,
)
