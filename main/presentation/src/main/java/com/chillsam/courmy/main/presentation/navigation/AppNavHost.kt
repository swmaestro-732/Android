package com.chillsam.courmy.main.presentation.navigation

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.NavSignal
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.jank.JankPageEffect
import com.chillsam.courmy.common.presentation.telemetry.TelemetryScreenEffect
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.onboarding.SplashPage

@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    val activity = LocalActivity.current
    val exitApp = { activity?.finish() ?: Unit }

    LaunchedEffect(Unit) {
        navigationHelper.navigationFlow.collect { signal ->
            when (signal) {
                is NavSignal.GoToDestPage -> handleNavRoute(signal.route, backStack)
                is NavSignal.DeepLink -> handleDeepLink(signal.route, backStack)
                is NavSignal.Replace -> handleReplace(signal.route, backStack)
                NavSignal.Back -> backStack.handleBack(exitApp)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.handleBack(exitApp) },
        modifier = modifier,
        transitionSpec = {
            // 스플래시가 들어오거나 나갈 때만 페이드(그 외 화면 전환은 즉시 유지).
            val fromPath = (initialState.entries.lastOrNull()?.contentKey as? GenericNavKey)?.path
            val toPath = (targetState.entries.lastOrNull()?.contentKey as? GenericNavKey)?.path
            if (fromPath == SplashPage.PATH || toPath == SplashPage.PATH) {
                fadeIn(tween(SPLASH_FADE_MS)) togetherWith fadeOut(tween(SPLASH_FADE_MS))
            } else {
                EnterTransition.None togetherWith ExitTransition.None
            }
        },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        // 단일 GenericNavKey 디스패처. 실제 화면 결정은 [appRouteByPath] 가 담당한다.
        entryProvider =
            entryProvider {
                entry<GenericNavKey> { navKey ->
                    val route = appRouteByPath[navKey.path]
                    if (route == null) {
                        Log.w(TAG, "Unknown path on render: ${navKey.path}")
                        LocalNavigationHelper.current.navigateTo(HomePage)
                        return@entry
                    }
                    // 페이지 식별자를 JankStats state 로 등록하고, 백스택 이탈 시 PAGE_EXIT flush.
                    JankPageEffect(navKey.path)
                    // 크래시 리포트에 남길 현재 화면. 전진·뒤로·교체·딥링크가 모두 여기를 지난다.
                    TelemetryScreenEffect(navKey.path)
                    route.render(navKey.args)
                }
            },
    )
}

private const val TAG = "[Navigation]"
private const val SPLASH_FADE_MS = 340

/**
 * NavRoute 한 건을 받아 백스택에 push(앱 내 전진 이동).
 * 동일 키가 이미 스택에 있으면 중복을 만들지 않고 최전면으로 끌어올린다 — Navigation3 의
 * contentKey 는 키별 1회만 유효하므로, 같은 (path,args) 키가 스택에 둘 이상 존재해선 안 된다.
 * 미등록 path 는 무시 + 경고 로그.
 */
fun handleNavRoute(
    route: NavRoute,
    backStack: NavBackStack<NavKey>,
) {
    if (appRouteByPath[route.path] == null) {
        Log.w(TAG, "Unhandled NavRoute: ${route.path}")
        return
    }
    val navKey = GenericNavKey.of(route)
    if (backStack.lastOrNull() != navKey) {
        backStack.bringToFront(navKey)
        Log.d(TAG, "navigateTo: $navKey")
    }
}

/**
 * 백스택을 대상 화면 하나로 교체한다(스플래시/온보딩처럼 뒤로 돌아오면 안 되는 진입 흐름).
 * 미등록 path 는 무시 + 경고 로그.
 */
fun handleReplace(
    route: NavRoute,
    backStack: NavBackStack<NavKey>,
) {
    if (appRouteByPath[route.path] == null) {
        Log.w(TAG, "Unhandled replace route: ${route.path}")
        return
    }
    backStack.clear()
    backStack.add(GenericNavKey.of(route))
    Log.d(TAG, "replace: ${route.path}")
}

/**
 * 웜 스타트 deep-link 처리.
 *
 * - 대상이 **bottom-tab 루트**면 콜드/in-app 과 동일한 탭 루트 시맨틱으로 처리한다
 *   ([handleNavRoute] 위임). 탭을 단순 bring-to-front 하면 탭이 루트에서 밀려나
 *   Back 동작/탭 루트가 깨지므로, 이 분기로 세 경로(콜드·웜·in-app)의 탭 백스택 형태를 일치시킨다.
 * - 그 외(leaf 화면)는 **bring-to-front**: 콜드 스타트의 synthetic 부모 체인
 *   ([com.chillsam.courmy.main.presentation.deeplink.resolveStartStack])과 달리, 이미 떠 있는
 *   사용자의 스택은 보존하고 대상 키만 최전면으로 올린다(동일 키는 중복 없이 최상단으로).
 */
fun handleDeepLink(
    route: NavRoute,
    backStack: NavBackStack<NavKey>,
) {
    val appRoute = appRouteByPath[route.path]
    if (appRoute == null) {
        Log.w(TAG, "Unhandled deep-link route: ${route.path}")
        return
    }
    if (appRoute.isBottomTab) {
        // 루트 탭은 in-app 탭 전환과 동일한 시맨틱으로(탭이 루트에서 밀려나지 않도록).
        handleNavRoute(route, backStack)
        return
    }
    val target = GenericNavKey.of(route)
    if (backStack.lastOrNull() == target) return // 이미 최전면 — no-op.
    backStack.bringToFront(target)
    Log.d(TAG, "deepLink bringToFront: $target")
}

/**
 * 동일 키(path+args)의 **모든** 출현을 제거한 뒤 최상단에 추가한다.
 * 백스택에 같은 키가 둘 이상 남지 않도록 보장한다(Navigation3 의 contentKey 중복 방지).
 * MutableList.remove 가 첫 출현만 지우는 것과 달리 removeAll 로 잔존 중복까지 제거한다.
 */
private fun NavBackStack<NavKey>.bringToFront(key: NavKey) {
    removeAll { it == key }
    add(key)
}

/**
 * 뒤로가기 정책: 백스택 최상단 한 단계만 pop 해 **직전 화면**으로 돌아간다.
 * (마이→설정→프로필 편집 처럼 여러 단계를 거친 경우 각 단계로 순서대로 복귀.)
 *
 * 루트(size==1)에서는 pop 하지 않고 [onExitApp] 으로 화면을 닫는다.
 * "스택을 비우면 시스템이 알아서 종료" 가 아니다 — NavDisplay 는 진입부에서
 * `require(backStack.isNotEmpty())` 를 검사하므로(navigation3-ui 1.1.1 NavDisplay.kt:360),
 * 스택을 비우면 다음 recomposition 에서 IllegalArgumentException 으로 죽는다.
 *
 * 시스템 뒤로가기는 NavDisplay 가 `isBackEnabled` 로 알아서 막지만, 화면 좌상단의 앱 자체
 * 뒤로 버튼([com.chillsam.courmy.common.presentation.helper.NavigationHelper.navigateToBack])은
 * 그 가드를 지나지 않으므로 여기서 막아야 한다.
 *
 * 딥링크로 들어온 경우 스택이 1칸이라 이 경로를 탄다([AppRoute.syntheticStack] 기본값).
 * 부모 화면으로 돌아가게 하려면 해당 라우트가 syntheticStack 으로 부모 체인을 선언해야 한다.
 */
private fun NavBackStack<NavKey>.handleBack(onExitApp: () -> Unit) {
    if (size > 1) removeLastOrNull() else onExitApp()
}
