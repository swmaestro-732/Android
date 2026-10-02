package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseDetailVO

/** 따라가기 화면 상태 변이 이벤트(FS-21). [CourseFollowViewModel.reduce] 한 곳에서만 소비한다. */
sealed interface CourseFollowReducerEvent : ReducerEvent {
    data object LoadStarted : CourseFollowReducerEvent

    data class Loaded(
        val detail: CourseDetailVO,
    ) : CourseFollowReducerEvent

    data class LoadFailed(
        val message: String,
    ) : CourseFollowReducerEvent

    /** 진행 시작. [startedAtMillis] 는 소요 시간 계산의 기준점이다. */
    data class Started(
        val startedAtMillis: Long,
    ) : CourseFollowReducerEvent

    /** 지금 장소 방문 처리. 마지막 장소였으면 [finishedAtMillis] 가 담겨 완주로 넘어간다. */
    data class Visited(
        val finishedAtMillis: Long?,
    ) : CourseFollowReducerEvent

    data object ErrorConsumed : CourseFollowReducerEvent
}
