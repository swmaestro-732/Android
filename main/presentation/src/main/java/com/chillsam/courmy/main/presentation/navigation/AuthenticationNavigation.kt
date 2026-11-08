package com.chillsam.courmy.main.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.onboarding.OnboardingPage
import com.chillsam.courmy.main.domain.onboarding.SplashPage

/** 기존 엔트리를 재생성하지 않아 ViewModel과 saveable 화면 상태를 그대로 유지한다. */
internal fun finishAuthentication(backStack: MutableList<NavKey>) {
    val loginIndex = backStack.indexOfFirst { (it as? GenericNavKey)?.path == LoginPage.PATH }
    if (loginIndex < 0) return
    val originPath = (backStack.getOrNull(loginIndex - 1) as? GenericNavKey)?.path
    if (originPath == null || originPath == OnboardingPage.PATH || originPath == SplashPage.PATH) {
        backStack.clear()
        backStack.add(GenericNavKey.of(HomePage.toRoute()))
        return
    }
    while (backStack.size > loginIndex) backStack.removeAt(backStack.lastIndex)
    if (originPath == GuestMyPage.PATH) {
        backStack.removeAt(backStack.lastIndex)
        backStack.removeAll { (it as? GenericNavKey)?.path == MyPage.PATH }
        backStack.add(GenericNavKey.of(MyPage.toRoute()))
    }
}

/**
 * 로그인 화면에서 "게스트로 돌아가기".
 *
 * 로그인 바로 앞이 온보딩/스플래시면(온보딩 건너뛰기·로그인하기로 올라온 경로) 돌아갈 게스트 홈이 없어,
 * 단순 Back 은 온보딩으로 복귀→다시 로그인으로 보내는 무한 루프가 된다. 이 경우 홈(게스트)으로 교체한다.
 * 게스트 홈(마이 등)에서 올라온 경우엔 로그인 이후 스택만 걷어내 그 화면으로 복귀한다.
 */
internal fun continueAsGuest(backStack: MutableList<NavKey>) {
    val loginIndex = backStack.indexOfFirst { (it as? GenericNavKey)?.path == LoginPage.PATH }
    if (loginIndex < 0) return
    val originPath = (backStack.getOrNull(loginIndex - 1) as? GenericNavKey)?.path
    if (originPath == null || originPath == OnboardingPage.PATH || originPath == SplashPage.PATH) {
        backStack.clear()
        backStack.add(GenericNavKey.of(HomePage.toRoute()))
        return
    }
    while (backStack.size > loginIndex) backStack.removeAt(backStack.lastIndex)
}
