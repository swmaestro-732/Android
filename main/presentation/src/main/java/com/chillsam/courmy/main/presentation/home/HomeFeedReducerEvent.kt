package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.home.HomeCourseVO

/** 홈 피드의 내부 상태 변경 이벤트. */
sealed interface HomeFeedReducerEvent : ReducerEvent {
    data object LoadStarted : HomeFeedReducerEvent

    data class Loaded(
        val courses: List<HomeCourseVO>,
        /** 저장 여부 표시용. 피드 응답에 저장 여부가 없어 따로 조회해 함께 싣는다. */
        val savedCourseIds: Set<String>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : HomeFeedReducerEvent

    data object LoadMoreStarted : HomeFeedReducerEvent

    /** 다음 페이지 도착. 기존 목록 뒤에 이어 붙인다. */
    data class MoreLoaded(
        val courses: List<HomeCourseVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : HomeFeedReducerEvent

    /** 다음 페이지 실패. 이미 보고 있는 목록은 그대로 두고 안내만 띄운다. */
    data class MoreFailed(
        val message: String,
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
