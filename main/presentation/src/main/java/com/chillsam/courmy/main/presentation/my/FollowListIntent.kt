package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 팔로우 목록(FS-15 O) 사용자 입력. */
sealed interface FollowListIntent : MviIntent {
    /** 탭 전환. 해당 탭을 아직 불러오지 않았으면 조회한다. */
    data class SelectTab(
        val tab: FollowTab,
    ) : FollowListIntent

    data object Retry : FollowListIntent

    /** 지금 탭의 목록 끝에 닿았을 때 다음 페이지 요청. */
    data object LoadMore : FollowListIntent

    /** 팔로잉 해제. 팔로워 탭에는 대응하는 서버 동작이 없어 쓰지 않는다. */
    data class Unfollow(
        val userId: Long,
    ) : FollowListIntent

    data object ConsumeError : FollowListIntent
}
