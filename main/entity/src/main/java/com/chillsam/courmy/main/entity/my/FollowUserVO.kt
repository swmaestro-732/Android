package com.chillsam.courmy.main.entity.my

import kotlinx.serialization.Serializable

/**
 * 팔로우 목록(FS-15 O)의 사용자 1건.
 * `GET /api/v1/users/{userId}/followers`·`/followings` 응답을 변환한 결과다.
 *
 * - [id]          사용자 식별자(팔로우/언팔로우 요청 키)
 * - [name]        표시 이름(닉네임)
 * - [handle]      핸들(@ 제외). 미설정 사용자는 빈 문자열이라 프로필로 이동할 수 없다
 * - [avatarUrl]   프로필 이미지 URL(없으면 placeholder)
 * - [isFollowing] 내가 이 사용자를 팔로우 중인지. 팔로잉 탭에서 해제 대상 판단에 쓴다
 * - [isMe]        이 사용자가 나인지. 내가 나를 팔로우하거나 목록에 섞여 들어온 경우,
 *                 눌렀을 때 타유저 프로필 대신 마이 화면으로 보내려고 둔다
 */
@Serializable
data class FollowUserVO(
    val id: Long,
    val name: String,
    val handle: String,
    val avatarUrl: String = "",
    val isFollowing: Boolean = false,
    val isMe: Boolean = false,
)
