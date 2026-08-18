package com.chillsam.courmy.main.presentation.login

import android.content.Context
import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface LoginIntent : MviIntent {
    /**
     * "카카오로 시작하기" 클릭. 카카오 SDK 로그인부터 서버 social-login 까지 한 흐름으로 처리한다.
     *
     * [context] 는 카카오 SDK 가 Activity 를 요구해서 전달할 뿐이고, ViewModel 은 이걸 **보관하지 않는다**
     * (필드로 들고 있으면 Activity 가 누수된다). 호출 인자로만 흘려보낸다.
     */
    data class KakaoLogin(
        val context: Context,
    ) : LoginIntent

    /** 내비게이션 신호 소비 후 상태 초기화. */
    data object ConsumeResult : LoginIntent

    /** 에러 메시지 소비. */
    data object ConsumeError : LoginIntent
}
