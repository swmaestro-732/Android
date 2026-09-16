package com.chillsam.courmy.main.domain.onboarding

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 스플래시 화면(FS-01). 앱 실행마다 잠깐 노출 후 온보딩 완료 여부에 따라 온보딩/홈으로 분기한다. */
object SplashPage : Page {
    const val PATH = "/splash"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
