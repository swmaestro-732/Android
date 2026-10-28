package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 오늘의 추천 코스 전체 목록 화면 식별자.
 *
 * 홈(FS-09)이 가로 미리보기 카드 몇 장만 남기도록 개편되면서,
 * 기존 홈의 공개 코스 피드 리스트 전체는 이 화면으로 옮겼다.
 * 홈의 "전체보기"에서 진입한다.
 */
object RecommendedCoursesPage : Page {
    const val PATH = "/home/recommended"

    /** 진입 시 선택해 둘 카테고리 라벨(예: "데이트"). 없으면 전체. */
    const val ARG_CATEGORY = "category"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    /** 홈 테마 타일에서 특정 카테고리가 선택된 상태로 진입할 때 쓴다. */
    fun route(category: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_CATEGORY to category),
        )
}
