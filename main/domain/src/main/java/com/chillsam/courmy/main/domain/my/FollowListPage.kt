package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 팔로우 목록 화면(FS-15 O). 마이의 팔로워/팔로잉 통계를 누르면 해당 탭으로 진입한다.
 * [route] 로 시작 탭([TAB_FOLLOWER]/[TAB_FOLLOWING])을 실어 전달한다.
 */
object FollowListPage : Page {
    const val PATH = "/my/follow"
    const val ARG_TAB = "tab"
    const val TAB_FOLLOWER = "follower"
    const val TAB_FOLLOWING = "following"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(tab: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_TAB to tab),
        )
}
