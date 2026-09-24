package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 검색 결과 화면(FS-18).
 *
 * 전체·장소·코스 세 탭이 상단바를 공유하고 목록만 바뀌므로 라우트는 하나만 둔다.
 * "장소 모두 보기"처럼 특정 탭으로 바로 들어가는 길이 있어 시작 탭을 인자로 받는다.
 */
object SearchResultPage : Page {
    const val PATH = "/search/result"
    const val ARG_QUERY = "query"
    const val ARG_TAB = "tab"

    const val TAB_ALL = "all"
    const val TAB_PLACE = "place"
    const val TAB_COURSE = "course"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(
        query: String,
        tab: String = TAB_ALL,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args =
                mapOf(
                    ARG_QUERY to query,
                    ARG_TAB to tab,
                ),
        )
}
