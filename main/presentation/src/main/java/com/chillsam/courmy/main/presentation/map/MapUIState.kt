package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapSortOrder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 지도 탭 메인 탐색(FS-10)의 화면 상태.
 *
 * [places] 는 이미 [category]·[sortOrder] 가 적용된 결과다. 화면은 걸러진 목록을 그대로 그린다.
 * [totalCount] 는 시트 제목("조건에 맞는 곳 28곳")에 쓰는 **전체** 개수라 [places] 크기와 다를 수 있다
 * — 시트 캐러셀은 앞쪽 일부만 보여주기 때문이다.
 */
data class MapUIState(
    val isLoading: Boolean = true,
    val bounds: MapBounds? = null,
    val places: ImmutableList<MapPlaceVO> = persistentListOf(),
    val clusters: ImmutableList<MapClusterVO> = persistentListOf(),
    val totalCount: Int = 0,
    val category: MapCategory = MapCategory.ALL,
    val sortOrder: MapSortOrder = MapSortOrder.SAVED,
    /** 말풍선이 떠 있는 핀. 아무것도 안 고르면 null. */
    val selectedPlaceId: Long? = null,
    /** 현위치 추적이 알려 준 기기 좌표(SCRUM-634). 권한 전·추적 전이면 null 이라 기본 기준점을 쓴다. */
    val myLat: Double? = null,
    val myLng: Double? = null,
    val errorMessage: String? = null,
) : UiState {
    val selectedPlace: MapPlaceVO?
        get() = places.firstOrNull { it.id == selectedPlaceId }

    companion object {
        val empty: MapUIState = MapUIState()
    }
}
