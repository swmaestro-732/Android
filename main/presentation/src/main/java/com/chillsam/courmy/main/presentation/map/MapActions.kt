package com.chillsam.courmy.main.presentation.map

import androidx.compose.runtime.Immutable
import com.chillsam.courmy.main.entity.map.MapBounds
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
    val onBoundsChanged: (MapBounds) -> Unit,
    val onRetry: () -> Unit,
    val onSearchClick: () -> Unit,
    val onFilterClick: () -> Unit,
    val onCategorySelect: (MapCategory) -> Unit,
    /** 핀·빈 지도 탭 — 선택만 바꾼다. 시트 캐러셀이 그 장소로 포커스되고 상세는 열지 않는다. */
    val onPlaceSelect: (Long?) -> Unit,
    /** 시트 카드 탭 — 장소 상세 시트를 연다. */
    val onPlaceDetail: (Long) -> Unit,
    val onSortSelect: (MapSortOrder) -> Unit,
    val onListClick: () -> Unit,
)

/** 검색 결과 화면이 상위로 올려 보내는 동작 묶음(FS-10-ResultMap / FS-10-List). */
@Immutable
data class MapSearchResultActions(
    val onBackClick: () -> Unit,
    val onQueryClick: () -> Unit,
    val onClearQuery: () -> Unit,
    val onPlaceSelect: (Long?) -> Unit,
    val onSaveToggle: (Long) -> Unit,
    val onRetry: () -> Unit,
    val onLoadMore: () -> Unit,
    val onSheetExpandedChange: (Boolean) -> Unit,
)
