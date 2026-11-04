package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 지역별 둘러보기 화면 식별자.
 *
 * 홈의 "지역별 둘러보기" 진입점에서 들어온다. 전체 화면 전국 지도에서 시·도를 눌러 확대하고,
 * 시·군·구를 누르면 그 지역 코스 목록([RecommendedCoursesPage.regionRoute])으로 이동한다.
 */
object RegionBrowsePage : Page {
    const val PATH = "/home/region-browse"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
