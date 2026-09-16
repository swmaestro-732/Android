package com.chillsam.courmy.main.domain.login

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 로그인·시작하기 화면(FS-03). 소셜 로그인 진입점. 앱 어디서든 로그인/회원가입 버튼은 이 화면으로 온다. */
object LoginPage : Page {
    const val PATH = "/login"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
