package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * 화면이 다시 보일 때마다 [onRefresh] 를 호출한다.
 *
 * 하단 탭 이동·상세 화면에서 복귀처럼 백스택에 남아 있던 화면으로 돌아오면, 그 사이에 추가·삭제된
 * 항목이 반영되지 않는다(ViewModel 이 살아 있어 다시 로드하지 않는다). 그 간극을 메운다.
 *
 * **첫 resume 은 건너뛴다.** ViewModel 이 init 에서 이미 불러오므로, 진입 시점에 호출하면
 * 같은 요청이 두 번 나간다.
 */
@Composable
fun RefreshOnResume(onRefresh: () -> Unit) {
    // 리컴포지션마다 새로 만들어지는 람다라도 항상 최신 것을 부르도록 잡아 둔다.
    val currentOnRefresh by rememberUpdatedState(onRefresh)
    var skipFirstResume by remember { mutableStateOf(true) }
    LifecycleResumeEffect(Unit) {
        if (skipFirstResume) {
            skipFirstResume = false
        } else {
            currentOnRefresh()
        }
        onPauseOrDispose {}
    }
}
