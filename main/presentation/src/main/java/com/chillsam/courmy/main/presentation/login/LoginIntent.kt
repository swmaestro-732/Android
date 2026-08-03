package com.chillsam.courmy.main.presentation.login

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface LoginIntent : MviIntent {
    /** 카카오 SDK 로 받은 idToken 으로 서버 로그인. */
    data class SocialLogin(
        val idToken: String,
    ) : LoginIntent

    /** 내비게이션 신호 소비 후 상태 초기화. */
    data object ConsumeResult : LoginIntent

    /** 에러 메시지 소비. */
    data object ConsumeError : LoginIntent
}
