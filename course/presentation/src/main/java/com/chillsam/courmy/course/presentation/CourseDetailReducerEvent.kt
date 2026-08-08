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

    data object SaveStarted : CourseDetailReducerEvent

    /** 저장/취소가 서버에서 확정됨. 상세의 저장 상태를 [saved] 로 바꾼다. */
    data class SaveFinished(
        val saved: Boolean,
    ) : CourseDetailReducerEvent

    data class SaveFailed(
        val message: String,
    ) : CourseDetailReducerEvent

    data object FollowStarted : CourseDetailReducerEvent

    data class FollowFinished(
        val following: Boolean,
    ) : CourseDetailReducerEvent

    data class FollowFailed(
        val message: String,
    ) : CourseDetailReducerEvent

    data object ErrorConsumed : CourseDetailReducerEvent

    data object DeleteStarted : CourseDetailReducerEvent

    /** 삭제 확정 — 화면이 이 값을 보고 이전 화면으로 돌아간다. */
    data object Deleted : CourseDetailReducerEvent

    data class DeleteFailed(
        val message: String,
    ) : CourseDetailReducerEvent
}
