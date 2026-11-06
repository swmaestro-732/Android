package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeInProgressCourseVO
import com.chillsam.courmy.main.entity.home.HomeNearbyPlaceVO

/** 홈 화면의 내부 상태 변경 이벤트. */
sealed interface HomeReducerEvent : ReducerEvent {
    data object LoadStarted : HomeReducerEvent

    data class Loaded(
        val courses: List<HomeCourseVO>,
        val nearbyPlaces: List<HomeNearbyPlaceVO>,
        val inProgressCourse: HomeInProgressCourseVO?,
    ) : HomeReducerEvent

    data class Failed(
        val message: String,
    ) : HomeReducerEvent

    /** 진행 중인 코스 카드를 내린다(스톱). */
    data object InProgressDismissed : HomeReducerEvent

    /** 종료(end)가 서버에 실제로 기록됐다 — 화면이 저장함(완주)으로 보낸다. */
    data object InProgressEnded : HomeReducerEvent

    /** 종료 성공 알림 소비(저장함 이동 후). */
    data object InProgressEndedConsumed : HomeReducerEvent
}
