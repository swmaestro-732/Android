package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 관심 지역 편집 화면(FS-07). 관심 지역을 검색·선택한다. */
object InterestRegionPage : Page {
    const val PATH = "/my/interest/region"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
