package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO

/** 검색 결과 화면(FS-18)의 내부 상태 변경 이벤트. */
sealed interface SearchResultReducerEvent : ReducerEvent {
    data class LoadStarted(
        val query: String,
    ) : SearchResultReducerEvent

    data class Loaded(
        val places: List<MapPlaceVO>,
        val courses: List<SearchCourseVO>,
        val placeTotalCount: Int,
        val courseTotalCount: Int,
    ) : SearchResultReducerEvent

    data class Failed(
        val message: String,
    ) : SearchResultReducerEvent

    data class TabSelected(
        val tab: SearchResultTab,
    ) : SearchResultReducerEvent

    data class SaveToggled(
        val placeId: Long,
        val saved: Boolean,
    ) : SearchResultReducerEvent

    data object ErrorConsumed : SearchResultReducerEvent
}
