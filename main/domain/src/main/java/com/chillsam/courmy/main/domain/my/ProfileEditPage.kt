package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 프로필 편집 화면(FS-26). 닉네임·아이디·소개·관심 테마/지역을 편집한다. */
object ProfileEditPage : Page {
    const val PATH = "/my/profile/edit"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
