package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 팔로우 목록 화면(FS-15 O). 프로필의 팔로워/팔로잉 통계를 누르면 해당 탭으로 진입한다.
 * [route] 로 시작 탭([TAB_FOLLOWER]/[TAB_FOLLOWING])을 실어 전달한다.
 *
 * [ARG_USER_ID] 를 실으면 그 사용자의 목록을, 없으면 내 목록을 본다(서버 API 가 userId 를 받는다).
 */
object FollowListPage : Page {
    const val PATH = "/my/follow"
    const val ARG_TAB = "tab"
    const val ARG_USER_ID = "userId"
    const val TAB_FOLLOWER = "follower"
    const val TAB_FOLLOWING = "following"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    /** [userId] 가 null 이면 내 목록. 타유저 프로필에서 넘어올 때만 값을 싣는다. */
    fun route(
        tab: String,
        userId: Long? = null,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args =
                buildMap {
                    put(ARG_TAB, tab)
                    userId?.let { put(ARG_USER_ID, it.toString()) }
                },
        )
}
