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
