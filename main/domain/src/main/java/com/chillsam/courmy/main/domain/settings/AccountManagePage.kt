package com.chillsam.courmy.main.domain.settings

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 계정 관리 화면. 설정 목록에서 진입하며 로그아웃·회원 탈퇴를 담당한다. */
object AccountManagePage : Page {
    const val PATH = "/settings/account"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
