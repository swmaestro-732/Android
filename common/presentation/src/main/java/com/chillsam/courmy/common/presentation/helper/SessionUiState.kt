package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 앱 전역 로그인 세션 상태(프로토타입). 실제 인증 연동 전까지 전체 플로우 데모용으로,
 * 게스트↔로그인 화면 전환과 로그인 전용 UI(홈 코스 만들기 FAB 등) 노출을 제어한다.
 *
 * [initialLoggedIn] 은 생성 시점의 실제 로그인 여부(토큰 보유)로 시딩한다. Activity 가 재생성되면
 * (언어 변경 등) 이 상태가 새로 만들어지는데, 기본값 false 로 두면 스플래시를 다시 거치지 않는 한
 * 로그인했던 사용자가 로그아웃된 것처럼 보인다. 그래서 토큰 기준으로 시딩해 재생성에도 유지한다.
 */
class SessionUiState(
    initialLoggedIn: Boolean = false,
) {
    var isLoggedIn by mutableStateOf(initialLoggedIn)
        private set

    var loginRequired by mutableStateOf(false)
        private set

    fun requireLogin(action: () -> Unit) {
        if (isLoggedIn) action() else loginRequired = true
    }

    fun dismissLoginRequired() {
        loginRequired = false
    }

    fun login() {
        dismissLoginRequired()
        isLoggedIn = true
    }

    fun logout() {
        dismissLoginRequired()
        isLoggedIn = false
    }
}

/** 세션 상태 주입용 CompositionLocal. [com.chillsam.courmy.main.presentation] RootComposable 에서 provide 한다. */
val LocalSessionUiState =
    staticCompositionLocalOf<SessionUiState> {
        error("No SessionUiState provided")
    }
