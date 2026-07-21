package com.chillsam.courmy.main.domain.course

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 임시저장한 코스 목록 화면. */
object DraftListPage : Page {
    const val PATH = "/course/drafts"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
