package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.my.FollowUserVO

/** 팔로우 목록의 내부 상태 변경 이벤트. */
sealed interface FollowListReducerEvent : ReducerEvent {
    data class TabSelected(
        val tab: FollowTab,
    ) : FollowListReducerEvent

    data object LoadStarted : FollowListReducerEvent

    data class Loaded(
        val tab: FollowTab,
        val users: List<FollowUserVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : FollowListReducerEvent

    data object LoadMoreStarted : FollowListReducerEvent

    /** 다음 페이지 도착. 해당 탭 목록 뒤에 이어 붙인다. */
    data class MoreLoaded(
        val tab: FollowTab,
        val users: List<FollowUserVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : FollowListReducerEvent

    data class MoreFailed(
        val message: String,
    ) : FollowListReducerEvent

    data class LoadFailed(
        val message: String,
    ) : FollowListReducerEvent

    /** 언팔로우 확정 — 팔로잉 목록에서 뺀다. */
    data class Unfollowed(
        val userId: Long,
    ) : FollowListReducerEvent

    data class ActionFailed(
        val message: String,
    ) : FollowListReducerEvent

    data object ErrorConsumed : FollowListReducerEvent
}
