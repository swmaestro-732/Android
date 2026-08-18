package com.chillsam.courmy.main.domain.onboarding

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 온보딩 화면(FS-02). 앱 최초 실행 시 1회 노출하는 3컷 인트로 + 시작 진입. */
object OnboardingPage : Page {
    const val PATH = "/onboarding"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
