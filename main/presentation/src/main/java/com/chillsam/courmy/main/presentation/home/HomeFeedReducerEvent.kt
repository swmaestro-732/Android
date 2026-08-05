package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.home.HomeCourseVO

/** 홈 피드의 내부 상태 변경 이벤트. */
sealed interface HomeFeedReducerEvent : ReducerEvent {
    data object LoadStarted : HomeFeedReducerEvent

    data class Loaded(
        val courses: List<HomeCourseVO>,
    ) : HomeFeedReducerEvent

    data class Failed(
        val message: String,
    ) : HomeFeedReducerEvent
}
