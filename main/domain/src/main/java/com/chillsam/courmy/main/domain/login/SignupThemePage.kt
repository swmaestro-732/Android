package com.chillsam.courmy.main.domain.login

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 회원가입 플로우의 관심 테마 선택(FS-06). 편집용 관심 테마 화면을 "다음" 흐름으로 재사용한다. */
object SignupThemePage : Page {
    const val PATH = "/login/theme"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
