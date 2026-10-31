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

    data object LikeToggled : CourseDetailReducerEvent

    /**
     * 좋아요 토글이 서버에서 확정됨. 낙관적으로 먼저 바꾼 화면 값을 서버 집계로 보정한다
     * (다른 사용자의 좋아요가 섞이면 로컬 ±1 과 어긋날 수 있다).
     */
    data class LikeConfirmed(
        val liked: Boolean,
        val likeCount: Int,
    ) : CourseDetailReducerEvent

    /** 좋아요 토글 실패 — 낙관적으로 바꿨던 값을 토글 전으로 되돌리고 한 번 알린다. */
    data class LikeFailed(
        val message: String,
        val liked: Boolean,
        /** 토글 전 집계. 아직 서버 집계를 받은 적이 없으면 null(숫자 미표시 상태로 복귀). */
        val likeCount: Int?,
    ) : CourseDetailReducerEvent

    /** 같은 ViewModel 인스턴스로 다른 코스를 열 때 로컬 소셜 상태를 비운다. */
    data object SocialStateReset : CourseDetailReducerEvent

    data object ErrorConsumed : CourseDetailReducerEvent

    /** 비로그인 상태로 로그인이 필요한 동작을 눌렀다. 화면이 안내 다이얼로그를 띄운다. */
    data object LoginRequired : CourseDetailReducerEvent

    data object LoginRequiredConsumed : CourseDetailReducerEvent

    /** 포크 진입이 확정됐다(로그인 확인 통과). 화면이 이 값을 보고 코스 만들기로 이동한다. */
    data object ForkRequested : CourseDetailReducerEvent

    data object ForkRequestConsumed : CourseDetailReducerEvent

    data object DeleteStarted : CourseDetailReducerEvent

    /** 삭제 확정 — 화면이 이 값을 보고 이전 화면으로 돌아간다. */
    data object Deleted : CourseDetailReducerEvent

    data class DeleteFailed(
        val message: String,
    ) : CourseDetailReducerEvent
}
