package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseDetailVO

/**
 * reduce() 에 입력되는 내부 이벤트. ViewModel 이 Intent/코루틴 결과를 이 이벤트로 변환한다.
 */
sealed interface CourseDetailReducerEvent : ReducerEvent {
    data object LoadStarted : CourseDetailReducerEvent

    data class Loaded(
        val detail: CourseDetailVO,
    ) : CourseDetailReducerEvent

    data class Failed(
        val message: String,
    ) : CourseDetailReducerEvent
}
