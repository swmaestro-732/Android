package com.chillsam.courmy.common.presentation.telemetry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import com.chillsam.courmy.common.domain.telemetry.NoOpTelemetry
import com.chillsam.courmy.common.domain.telemetry.Telemetry

/**
 * 미제공 시 [NoOpTelemetry] 로 떨어진다.
 *
 * 다른 CompositionLocal 들(`LocalJankReporter` 등)은 미제공을 error 로 막지만, 여기는 다르게 둔다.
 * 기록이 빠지는 건 기능 손실이 아니지만, @Preview 나 테스트에서 화면이 죽는 건 손실이기 때문이다.
 */
val LocalTelemetry = compositionLocalOf<Telemetry> { NoOpTelemetry }

/**
 * 현재 화면을 [Telemetry] 에 기록한다. 크래시 리포트에 "어느 화면에서 죽었는지"와
 * 거기까지 온 이동 경로가 함께 남는다.
 *
 * 사용 위치: [com.chillsam.courmy.main.presentation.navigation.AppNavHost] 의 백스택 최상위가
 * 바뀔 때마다 호출된다. 전진·뒤로·교체·딥링크가 모두 이 지점을 지나므로, 네비게이션 호출부에
 * 거는 것과 달리 **실제로 보이는 화면**과 어긋나지 않는다.
 *
 * @param pagePath [com.chillsam.courmy.common.domain.navigation.NavRoute.path]. 식별자(코스 id 등)는
 *  `args` 에 따로 있어 여기 담기지 않으므로, 그대로 남겨도 개인정보가 새지 않는다.
 */
@Composable
fun TelemetryScreenEffect(pagePath: String) {
    val telemetry = LocalTelemetry.current
    LaunchedEffect(pagePath) { telemetry.setScreen(pagePath) }
}
