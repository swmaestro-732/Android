package com.chillsam.courmy.main.domain.settings

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 설치된 앱 버전과 업데이트 가능 여부를 확인하는 설정 상세. */
object AppVersionPage : Page {
    const val PATH = "/settings/app-version"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
