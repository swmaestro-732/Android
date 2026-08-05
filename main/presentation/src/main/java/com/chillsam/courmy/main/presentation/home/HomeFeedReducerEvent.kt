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

    data class SaveStarted(
        val courseId: String,
    ) : HomeFeedReducerEvent

    /** 저장/취소가 서버에서 확정됨. */
    data class SaveFinished(
        val courseId: String,
        val saved: Boolean,
    ) : HomeFeedReducerEvent

    data class SaveFailed(
        val courseId: String,
        val message: String,
    ) : HomeFeedReducerEvent

    data object ErrorConsumed : HomeFeedReducerEvent
}
