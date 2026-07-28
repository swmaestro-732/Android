package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 앱 전역 로그인 세션 상태(프로토타입). 실제 인증 연동 전까지 전체 플로우 데모용으로,
 * 게스트↔로그인 화면 전환과 로그인 전용 UI(홈 코스 만들기 FAB 등) 노출을 제어한다.
 */
class SessionUiState {
    var isLoggedIn by mutableStateOf(false)
        private set

    fun login() {
        isLoggedIn = true
    }

    fun logout() {
        isLoggedIn = false
    }
}

/** 세션 상태 주입용 CompositionLocal. [com.chillsam.courmy.main.presentation] RootComposable 에서 provide 한다. */
val LocalSessionUiState =
    staticCompositionLocalOf<SessionUiState> {
        error("No SessionUiState provided")
    }
