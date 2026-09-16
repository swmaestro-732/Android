package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.DraftSummaryVO

/** reduce() 에 입력되는 내부 이벤트. ViewModel 이 Intent/코루틴 결과를 이 이벤트로 변환한다. */
sealed interface DraftListReducerEvent : ReducerEvent {
    data object LoadStarted : DraftListReducerEvent

    data class Loaded(
        val drafts: List<DraftSummaryVO>,
    ) : DraftListReducerEvent

    /** 목록 조회 실패. 빈 목록과 구분해 "다시 시도"를 띄운다. */
    data class LoadFailed(
        val message: String,
    ) : DraftListReducerEvent

    /** 삭제 성공. 목록을 다시 부르지 않고 지운 항목만 걷어낸다(왕복 한 번을 아낀다). */
    data class Deleted(
        val courseId: Long,
    ) : DraftListReducerEvent

    /** 삭제 실패. 목록은 그대로 두고 안내만 띄운다. */
    data class DeleteFailed(
        val message: String,
    ) : DraftListReducerEvent

    data object ErrorConsumed : DraftListReducerEvent
}
