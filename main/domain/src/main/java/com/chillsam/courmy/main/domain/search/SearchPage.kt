package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 검색어 입력 화면(FS-17). 홈 검색바를 누르면 열리고, 최근 검색어와 급상승 검색어를 보여준다. */
object SearchPage : Page {
    const val PATH = "/search"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
