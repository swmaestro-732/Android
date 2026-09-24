package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort

/** 후기 전체보기 화면의 내부 상태 변경 이벤트. */
sealed interface CourseReviewListReducerEvent : ReducerEvent {
    /** 첫 페이지 조회 시작. 이전 목록을 비운다. */
    data object LoadStarted : CourseReviewListReducerEvent

    /** 첫 페이지 도착. 집계와 목록을 모두 갈아 끼운다. */
    data class Loaded(
        val list: CourseReviewListVO,
        /** 조회 시점의 로그인 상태. 작성 버튼 노출에 쓴다. */
        val isLoggedIn: Boolean,
    ) : CourseReviewListReducerEvent

    data class LoadFailed(
        val message: String,
    ) : CourseReviewListReducerEvent

    /** 정렬이 바뀌었다. 목록은 [LoadStarted] 로 새로 받는다. */
    data class SortChanged(
        val sort: CourseReviewSort,
    ) : CourseReviewListReducerEvent

    data object LoadMoreStarted : CourseReviewListReducerEvent

    /** 다음 페이지 도착. 집계는 그대로 두고 목록만 잇는다. */
    data class MoreLoaded(
        val page: CourseReviewListVO,
    ) : CourseReviewListReducerEvent

    /**
     * 다음 페이지 조회 실패. 이미 보고 있는 목록이 있으니 화면 전체를 에러로 바꾸지 않고
     * 스낵바로만 알린 뒤 다시 시도할 수 있게 둔다.
     */
    data class LoadMoreFailed(
        val message: String,
    ) : CourseReviewListReducerEvent

    data class DeleteStarted(
        val reviewId: Long,
    ) : CourseReviewListReducerEvent

    /** 삭제 성공. 목록에서 빼고 총 개수를 다시 받기 전까지는 화면만 먼저 줄인다. */
    data class Deleted(
        val reviewId: Long,
    ) : CourseReviewListReducerEvent

    data class DeleteFailed(
        val message: String,
    ) : CourseReviewListReducerEvent

    /** 스낵바 등으로 한 번 보여준 안내를 지운다. */
    data object MessageConsumed : CourseReviewListReducerEvent
}
