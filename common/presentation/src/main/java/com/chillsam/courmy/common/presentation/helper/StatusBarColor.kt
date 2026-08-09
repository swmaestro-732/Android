package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 상태바 영역에 칠할 색. 화면이 정하고 RootComposable 이 그린다.
 *
 * targetSdk 35 부터 `window.statusBarColor` 가 무시돼 색을 직접 그려야 하는데, 한 색으로 고정하면
 * 상단이 흰 화면(프로필 편집 등)에서 띠만 회색으로 남아 분리돼 보인다. 그래서 화면이 "내 상단 색"을
 * 알려 주고, 시스템 아이콘 명암은 그 색의 휘도로 자동 결정한다.
 *
 * [color] 가 null 이면 앱 기본 배경색을 쓴다.
 */
class StatusBarState {
    var color: Color? by mutableStateOf(null)
        private set

    fun set(value: Color) {
        color = value
    }

    fun reset(value: Color) {
        // 다른 화면이 이미 자기 색으로 바꿔 놨으면 되돌리지 않는다(전환 중 순서가 엇갈릴 수 있다).
        if (color == value) color = null
    }
}

val LocalStatusBarState =
    staticCompositionLocalOf<StatusBarState> {
        error("No StatusBarState provided")
    }

/**
 * 이 화면(또는 상단바 컴포넌트)이 보이는 동안 상태바를 [color] 로 칠한다.
 *
 * 상단이 앱 기본 배경색인 화면은 부를 필요가 없다 — 그게 기본값이다.
 */
@Composable
fun StatusBarColor(color: Color) {
    val state = LocalStatusBarState.current
    DisposableEffect(state, color) {
        state.set(color)
        onDispose { state.reset(color) }
    }
}
