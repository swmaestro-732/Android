package com.chillsam.courmy.main.presentation.user

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.user.FollowRelation
import com.chillsam.courmy.main.entity.user.UserProfileVO

sealed interface UserProfileReducerEvent : ReducerEvent {
    data object LoadStarted : UserProfileReducerEvent

    data class Loaded(
        val profile: UserProfileVO,
    ) : UserProfileReducerEvent

    data class Failed(
        val message: String,
    ) : UserProfileReducerEvent

    /** 팔로우 요청 진행 중(버튼 중복 탭 방지). */
    data object FollowStarted : UserProfileReducerEvent

    /** 서버가 내려준 갱신 관계·팔로워 수로 프로필을 맞춘다. */
    data class FollowUpdated(
        val relation: FollowRelation,
        val followerCount: String,
    ) : UserProfileReducerEvent

    data class FollowFailed(
        val message: String,
    ) : UserProfileReducerEvent

    data object FollowErrorConsumed : UserProfileReducerEvent

    /** 비로그인 상태로 팔로우를 눌렀다. 화면이 로그인 안내를 띄운다. */
    data object LoginRequired : UserProfileReducerEvent

    data object LoginRequiredConsumed : UserProfileReducerEvent
}
