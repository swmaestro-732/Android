package com.chillsam.courmy.main.presentation.login

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

sealed interface LoginReducerEvent : ReducerEvent {
    data object Started : LoginReducerEvent

    data class Succeeded(
        val newUser: Boolean,
    ) : LoginReducerEvent

    data class Failed(
        val message: String,
    ) : LoginReducerEvent

    /**
     * 사용자가 카카오 로그인을 스스로 취소했다. 실패가 아니므로 안내 문구 없이 로딩만 푼다.
     * (여기서 로딩을 안 풀면 버튼이 비활성인 채로 화면이 잠긴다)
     */
    data object Canceled : LoginReducerEvent

    data object ResultConsumed : LoginReducerEvent

    data object ErrorConsumed : LoginReducerEvent
}
