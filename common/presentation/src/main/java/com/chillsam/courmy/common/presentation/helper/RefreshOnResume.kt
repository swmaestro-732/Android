package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LifecycleResumeEffect

/**
 * 화면이 보일 때마다(첫 진입 포함) [onRefresh] 를 호출한다.
 *
 * 하단 탭 이동은 백스택에서 `bringToFront` 로 처리돼 **ViewModel 은 살아남고 컴포지션만 다시 만들어진다**.
 * 그래서 다른 화면에서 바꾼 값(프로필 수정, 코스 저장 등)이 탭으로 돌아왔을 때 반영되지 않는다.
 *
 * 첫 resume 을 건너뛰지 않는 이유: 건너뛰기 플래그를 [androidx.compose.runtime.remember] 로 들면
 * 탭 이동으로 컴포지션이 사라졌다 돌아올 때마다 초기화돼, 정작 필요한 순간에 새로고침이 걸리지 않는다.
 * 대신 **이 헬퍼를 쓰는 화면은 ViewModel 의 init 에서 로드하지 않는다** — 그래야 진입 시 한 번만 나간다.
 */
@Composable
fun RefreshOnResume(onRefresh: () -> Unit) {
    // 리컴포지션마다 새로 만들어지는 람다라도 항상 최신 것을 부르도록 잡아 둔다.
    val currentOnRefresh by rememberUpdatedState(onRefresh)
    LifecycleResumeEffect(Unit) {
        currentOnRefresh()
        onPauseOrDispose {}
    }
}
