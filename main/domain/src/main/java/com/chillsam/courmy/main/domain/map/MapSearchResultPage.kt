package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 지도 검색 결과 화면(FS-10-ResultMap / FS-10-List).
 *
 * 지도와 하단 시트는 한 화면이고, 시트가 **선택 장소 카드**인지 **결과 리스트**인지만 다르다.
 * 그래서 라우트도 하나만 두고 시트 형태는 화면 안의 상태로 다룬다.
 */
object MapSearchResultPage : Page {
    const val PATH = "/map/search"
    const val ARG_QUERY = "query"

    /** 시트를 리스트로 펼친 채 들어갈지 여부. 지도 탭의 목록 버튼이 이 값으로 진입한다. */
    const val ARG_LIST_MODE = "list"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(
        query: String,
        listMode: Boolean = false,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args =
                mapOf(
                    ARG_QUERY to query,
                    ARG_LIST_MODE to listMode.toString(),
                ),
        )
}
