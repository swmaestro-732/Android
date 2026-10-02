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

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
