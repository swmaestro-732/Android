package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 홈에서 진입하는 코스 검색 결과. */
object SearchResultPage : Page {
    const val PATH = "/search/result"
    const val ARG_QUERY = "query"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(query: String): NavRoute = NavRoute(PATH, mapOf(ARG_QUERY to query))
}
