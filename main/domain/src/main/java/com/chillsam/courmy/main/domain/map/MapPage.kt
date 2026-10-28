package com.chillsam.courmy.main.domain.map

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 지도 탭 메인 탐색 화면(FS-10). 현재 위치 주변 장소를 지도와 하단 시트로 함께 보여준다. */
object MapPage : Page {
    const val PATH = "/map"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
