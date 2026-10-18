package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** 상태바 영역에 칠할 색. 화면이 정하고 RootComposable이 그린다. */
class StatusBarState {
    var color: Color? by mutableStateOf(null)
        private set

    private var owner: Any? = null

    fun set(
        owner: Any,
        value: Color,
    ) {
        this.owner = owner
        color = value
    }

    fun reset(owner: Any) {
        // 다른 화면이 이미 자기 색을 칠했으면(소유자가 바뀜) 이전 화면이 초기화하지 않는다.
        // 색 "값"으로 가드하면 같은 색을 쓰는 화면끼리 전환할 때(흰색→흰색) 이전 화면의
        // dispose 가 새 화면이 칠한 색을 지워 기본 회색으로 떨어진다.
        if (this.owner === owner) {
            this.owner = null
            color = null
        }
    }
}

val LocalStatusBarState =
    staticCompositionLocalOf<StatusBarState> {
        error("No StatusBarState provided")
    }

/** 이 화면이 보이는 동안 상태바를 [color]로 칠한다. */
@Composable
fun StatusBarColor(color: Color) {
    val state = LocalStatusBarState.current
    // 화면 인스턴스마다 고유한 소유자 토큰. 색이 같아도 "누가 칠했는지"를 구분한다.
    val owner = remember { Any() }
    DisposableEffect(state, color) {
        state.set(owner, color)
        onDispose { state.reset(owner) }
    }
}
