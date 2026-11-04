package com.chillsam.courmy.main.domain.chat

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 채팅 탭 메인 화면. 하단 네비게이션 가운데 탭에서 진입하는 대화방이다. */
object ChatPage : Page {
    const val PATH = "/chat"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
