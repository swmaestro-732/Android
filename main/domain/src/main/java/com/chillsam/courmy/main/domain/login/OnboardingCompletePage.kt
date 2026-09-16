package com.chillsam.courmy.main.domain.login

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 온보딩 완료 화면(FS-08). 취향 기반 추천 코스를 보여주고 "Courmy 시작하기"로 로그인 완료·홈 진입. */
object OnboardingCompletePage : Page {
    const val PATH = "/login/complete"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
