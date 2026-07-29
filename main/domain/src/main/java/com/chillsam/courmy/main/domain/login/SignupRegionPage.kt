package com.chillsam.courmy.main.domain.login

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 회원가입 플로우의 관심 지역 선택(FS-07). 편집용 관심 지역 화면을 "다음" 흐름으로 재사용한다. */
object SignupRegionPage : Page {
    const val PATH = "/login/region"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
