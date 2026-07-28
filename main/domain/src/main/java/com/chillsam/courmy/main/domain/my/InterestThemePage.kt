package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 관심 테마 편집 화면(FS-06). 관심 테마 칩을 선택한다. */
object InterestThemePage : Page {
    const val PATH = "/my/interest/theme"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
