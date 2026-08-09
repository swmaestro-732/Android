package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** 상태바 영역에 칠할 색. 화면이 정하고 RootComposable이 그린다. */
class StatusBarState {
    var color: Color? by mutableStateOf(null)
        private set

    fun set(value: Color) {
        color = value
    }

    fun reset(value: Color) {
        // 다른 화면이 이미 자기 색으로 바꿨으면 이전 화면이 초기화하지 않는다.
        if (color == value) color = null
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
    DisposableEffect(state, color) {
        state.set(color)
        onDispose { state.reset(color) }
    }
}
