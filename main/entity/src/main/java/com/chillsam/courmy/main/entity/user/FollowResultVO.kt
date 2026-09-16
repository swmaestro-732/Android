package com.chillsam.courmy.main.entity.user

import kotlinx.serialization.Serializable

/**
 * 팔로우/언팔로우 직후의 대상 사용자 상태.
 * 서버가 갱신된 팔로워 수를 함께 내려주므로, 화면은 낙관적 갱신 대신 이 값으로 통계를 맞춘다.
 */
@Serializable
data class FollowResultVO(
    val isFollowing: Boolean,
    val followerCount: String,
)
