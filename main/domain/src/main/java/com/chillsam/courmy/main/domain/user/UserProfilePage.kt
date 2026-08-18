package com.chillsam.courmy.main.domain.user

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 타유저 프로필 화면(FS-15 OtherUserPageActivity).
 *
 * 서버 조회 경로가 `GET /service/v1/mypage/{handle}` 이라 식별자는 userId 가 아니라 **handle** 이다.
 * 팔로우 목록·코스 작성자 등 handle 을 아는 지점에서 [route] 로 진입한다.
 */
object UserProfilePage : Page {
    const val PATH = "/user/profile"
    const val ARG_HANDLE = "handle"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(handle: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_HANDLE to handle),
        )
}
