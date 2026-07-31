package com.chillsam.courmy.main.entity.my

import kotlinx.serialization.Serializable

/**
 * 팔로우 목록(FS-15 O)의 사용자 1건.
 *
 * - [id]        사용자 식별자
 * - [name]      표시 이름(예: "User1")
 * - [handle]    핸들(@ 제외, 예: "happy")
 * - [avatarUrl] 프로필 이미지 URL(없으면 placeholder)
 */
@Serializable
data class FollowUserVO(
    val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String = "",
) {
    companion object {
        /** 개발/프리뷰용 더미 팔로워 목록(백엔드 미연동 시 사용). */
        val sampleFollowers: List<FollowUserVO> =
            listOf(
                FollowUserVO(id = "u_happy", name = "User1", handle = "happy"),
                FollowUserVO(id = "u_goodluck", name = "User3", handle = "good_luck"),
            )

        /** 개발/프리뷰용 더미 팔로잉 목록(백엔드 미연동 시 사용). */
        val sampleFollowing: List<FollowUserVO> =
            listOf(
                FollowUserVO(id = "u_happy", name = "User1", handle = "happy"),
                FollowUserVO(id = "u_newyear", name = "User2", handle = "new_year"),
            )
    }
}
