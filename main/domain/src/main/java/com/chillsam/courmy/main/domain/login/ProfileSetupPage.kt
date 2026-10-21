package com.chillsam.courmy.main.domain.login

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 프로필·아이디 설정 화면(FS-05). 회원가입 플로우에서 닉네임·아이디를 정한다. */
object ProfileSetupPage : Page {
    const val PATH = "/login/profile"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
