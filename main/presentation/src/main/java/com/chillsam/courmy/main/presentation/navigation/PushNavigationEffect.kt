package com.chillsam.courmy.main.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.domain.push.PushTarget
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.SessionUiState
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.onboarding.SplashPage

@Composable
internal fun PushNavigationEffect(
    target: PushTarget?,
    currentPath: String?,
    sessionState: SessionUiState,
    blocked: Boolean,
    onConsumed: () -> Unit,
) {
    if (target == null) return
    val viewModel: PushSessionViewModel = hiltViewModel()
    val ready by viewModel.sessionReady.collectAsStateWithLifecycle()
    val navigation = LocalNavigationHelper.current
    var loginRequested by remember(target) { mutableStateOf(false) }
    LaunchedEffect(target, currentPath, sessionState.isLoggedIn, blocked, ready) {
        if (!ready || blocked) return@LaunchedEffect
        if (currentPath == SplashPage.PATH || currentPath == null) return@LaunchedEffect
        // 로그인/가입이 Home으로 교체하기 전에 목적지를 보내면 그 이동에 덮어쓰인다.
        if (currentPath.startsWith(LoginPage.PATH)) return@LaunchedEffect
        if (!viewModel.isLoggedIn) {
            if (!loginRequested) {
                loginRequested = true
                navigation.navigateTo(LoginPage)
            }
            return@LaunchedEffect
        }
        sessionState.login()
        viewModel.markRead(target.notificationId)
        if (target.targetType == "COURSE") {
            navigation.navigateDeepLink(CourseDetailPage.route(target.targetId.toString()))
        } else {
            navigation.navigateDeepLink(
                com.chillsam.courmy.main.domain.notification.NotificationInboxPage
                    .toRoute(),
            )
        }
        onConsumed()
    }
}
