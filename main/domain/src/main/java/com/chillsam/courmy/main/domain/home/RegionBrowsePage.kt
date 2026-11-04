package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 지역별 둘러보기 화면 식별자.
 *
 * 홈의 "지역별 둘러보기"에서 들어온다. 인자 없이 들어오면 전국 지도로 시작하고, 홈 타일 지도에서
 * 특정 시·도를 눌러 들어오면 그 시·도로 확대된 상태([ARG_PROVINCE])로 시작해 바로 시·군·구를 고른다.
 * 시·군·구를 누르거나 "전체"를 누르면 그 지역 코스 목록([RecommendedCoursesPage.regionRoute])으로 이동한다.
 */
object RegionBrowsePage : Page {
    const val PATH = "/home/region-browse"

    /** 진입 시 확대해 둘 시·도 법정동 2자리 코드. 없으면 전국에서 시작. */
    const val ARG_PROVINCE = "province"

    /** 확대한 시·도의 표시 이름("전체" 선택 시 코스 목록 제목에 쓴다). */
    const val ARG_PROVINCE_NAME = "provinceName"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    /** 홈 타일 지도에서 특정 시·도로 들어올 때 쓴다. [code] 는 시·도 2자리, [name] 은 표시 이름. */
    fun focusRoute(
        code: String,
        name: String,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_PROVINCE to code, ARG_PROVINCE_NAME to name),
        )
}
