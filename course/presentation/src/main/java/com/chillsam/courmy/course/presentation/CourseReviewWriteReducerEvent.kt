package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

/** 후기 작성 화면의 내부 상태 변경 이벤트. */
sealed interface CourseReviewWriteReducerEvent : ReducerEvent {
    data class RatingChanged(
        val rating: Int,
    ) : CourseReviewWriteReducerEvent

    data class ContentChanged(
        val value: String,
    ) : CourseReviewWriteReducerEvent

    data class PhotosChanged(
        val uris: List<String>,
    ) : CourseReviewWriteReducerEvent

    data class TagToggled(
        val code: String,
    ) : CourseReviewWriteReducerEvent

    data object SubmitStarted : CourseReviewWriteReducerEvent

    data class SubmitSucceeded(
        val reviewId: Long,
    ) : CourseReviewWriteReducerEvent

    data class SubmitFailed(
        val message: String,
    ) : CourseReviewWriteReducerEvent

    data object ErrorConsumed : CourseReviewWriteReducerEvent

    /** 작성 성공을 전달했다. 입력을 비워 다음 작성이 깨끗한 상태로 시작하게 한다. */
    data object SubmittedConsumed : CourseReviewWriteReducerEvent
}
