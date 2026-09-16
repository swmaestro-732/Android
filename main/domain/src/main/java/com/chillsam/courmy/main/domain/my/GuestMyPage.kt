package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 비로그인 마이 화면(FS-15-Guest). 게스트 환영 + 로그인 유도. */
object GuestMyPage : Page {
    const val PATH = "/my/guest"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
