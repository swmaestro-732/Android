package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 홈의 코스 검색과 지도의 장소 검색이 공유하는 입력 화면. */
object SearchPage : Page {
    const val PATH = "/search"
    const val ARG_TARGET = "target"
    const val ARG_QUERY = "query"
    const val ARG_LATITUDE = "lat"
    const val ARG_LONGITUDE = "lng"
    const val TARGET_COURSE = "course"
    const val TARGET_PLACE = "place"

    override fun toRoute(): NavRoute = route()

    fun route(
        target: String = TARGET_COURSE,
        query: String = "",
        latitude: Double? = null,
        longitude: Double? = null,
    ): NavRoute =
        NavRoute(
            PATH,
            buildMap {
                put(ARG_TARGET, target)
                put(ARG_QUERY, query)
                latitude?.let { put(ARG_LATITUDE, it.toString()) }
                longitude?.let { put(ARG_LONGITUDE, it.toString()) }
            },
        )
}
