package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 마이 화면. 이번 실행 세션에서 저장한 코스 목록을 보여준다. */
object MyPage : Page {
    const val PATH = "/my"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
