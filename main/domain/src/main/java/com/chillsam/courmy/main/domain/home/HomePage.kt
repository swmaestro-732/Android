package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 앱의 기본 시작 화면 식별자.
 *
 * 템플릿에서 레퍼런스 feature 를 제거한 뒤 남는 최소 진입점으로,
 * 새 화면을 추가하기 전까지 콜드 스타트/미매칭 deep-link 의 fallback 목적지가 된다.
 */
object HomePage : Page {
    const val PATH = "/home"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
