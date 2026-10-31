package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeNearbyPlaceVO

/** 홈 화면의 내부 상태 변경 이벤트. */
sealed interface HomeReducerEvent : ReducerEvent {
    data object LoadStarted : HomeReducerEvent

    data class Loaded(
        val courses: List<HomeCourseVO>,
        val nearbyPlaces: List<HomeNearbyPlaceVO>,
    ) : HomeReducerEvent

    data class Failed(
        val message: String,
    ) : HomeReducerEvent
}
