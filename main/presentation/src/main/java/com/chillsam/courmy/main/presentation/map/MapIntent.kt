package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapSortOrder

/** 지도 탭 메인 탐색(FS-10)의 사용자 입력. */
sealed interface MapIntent : MviIntent {
    data class BoundsChanged(
        val bounds: MapBounds,
    ) : MapIntent

    data object Load : MapIntent

    data object Retry : MapIntent

    data class SelectCategory(
        val category: MapCategory,
    ) : MapIntent

    /** 현위치 추적이 알려 준 기기 좌표(SCRUM-634). 거리 기준점으로만 쓰고 카메라는 SDK 가 움직인다. */
    data class MyLocationChanged(
        val lat: Double,
        val lng: Double,
    ) : MapIntent

    /** 핀 탭. 이미 선택된 핀을 다시 누르거나 빈 지도를 누르면 null 로 선택을 푼다. */
    data class SelectPlace(
        val placeId: Long?,
    ) : MapIntent

    data class ChangeSortOrder(
        val order: MapSortOrder,
    ) : MapIntent

    data class ToggleSave(
        val placeId: Long,
    ) : MapIntent

    data object ConsumeError : MapIntent
}
