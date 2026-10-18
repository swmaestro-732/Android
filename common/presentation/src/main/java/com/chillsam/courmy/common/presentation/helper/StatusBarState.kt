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

    private val owners = mutableListOf<Pair<Any, Color>>()

    fun set(
        owner: Any,
        value: Color,
    ) {
        owners.removeAll { it.first === owner }
        owners.add(owner to value)
        color = value
    }

    fun reset(owner: Any) {
        owners.removeAll { it.first === owner }
        color = owners.lastOrNull()?.second
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
