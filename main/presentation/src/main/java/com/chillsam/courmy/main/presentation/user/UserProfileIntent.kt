package com.chillsam.courmy.main.presentation.user

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface UserProfileIntent : MviIntent {
    /** 화면 진입 시 최초 로드. 조회 키는 userId 가 아니라 handle 이다. */
    data class Load(
        val handle: String,
    ) : UserProfileIntent

    /** 에러 상태에서 재시도. */
    data object Retry : UserProfileIntent

    /** 코스 목록 끝에 닿아 다음 페이지를 이어 받는다(홈 피드와 같은 방식). */
    data object LoadMore : UserProfileIntent

    /** 팔로우 버튼 탭 — 현재 관계의 반대로 요청한다. */
    data object ToggleFollow : UserProfileIntent

    /** 팔로우 실패 안내를 노출한 뒤 상태에서 지운다. */
    data object ConsumeFollowError : UserProfileIntent

    /** 로그인 안내를 닫음(로그인하러 가든, 그만두든). */
    data object ConsumeLoginRequired : UserProfileIntent
}
