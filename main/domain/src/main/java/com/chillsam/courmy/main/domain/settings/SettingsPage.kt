package com.chillsam.courmy.main.domain.settings

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 설정 화면(FS-28). 프로필·알림·계정 관리. */
object SettingsPage : Page {
    const val PATH = "/settings"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
