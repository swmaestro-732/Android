package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseReviewListVO

/** 후기 전체보기 화면의 내부 상태 변경 이벤트. */
sealed interface CourseReviewListReducerEvent : ReducerEvent {
    data object LoadStarted : CourseReviewListReducerEvent

    data class Loaded(
        val list: CourseReviewListVO,
    ) : CourseReviewListReducerEvent

    data class LoadFailed(
        val message: String,
    ) : CourseReviewListReducerEvent

    data class SortChanged(
        val sort: CourseReviewSort,
    ) : CourseReviewListReducerEvent
}
