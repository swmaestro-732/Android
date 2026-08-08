package com.chillsam.courmy.main.data.follow.dto

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.my.FollowUserVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/users/{userId}/followers`·`/followings` 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * TODO-API-SPEC: 목록이 길어지면 커서 페이징을 붙인다. [wiki-needed]
 */
@Serializable
data class FollowListEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: FollowListDTO? = null,
)

@Serializable
data class FollowListDTO(
    val totalCount: Int? = null,
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val users: List<FollowUserDTO>? = null,
)

@Serializable
data class FollowUserDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
    val isFollowing: Boolean = false,
    val isFollower: Boolean = false,
)

fun FollowListDTO.toPageVO(): CursorPageVO<FollowUserVO> =
    CursorPageVO(
        items = toVOList(),
        nextCursor = nextCursor?.takeIf { it.isNotBlank() },
        // 커서가 없으면 더 받을 수 없으므로, 서버가 hasNext=true 로 줘도 끝으로 본다.
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

internal fun FollowListDTO.toVOList(): List<FollowUserVO> =
    users
        .orEmpty()
        // id 가 없으면 팔로우/언팔로우를 걸 수 없어 목록에 쓸 수 없다.
        .filter { it.id != null }
        .map { user ->
            FollowUserVO(
                id = user.id ?: 0L,
                name = user.nickname.orEmpty(),
                handle = user.handle.orEmpty(),
                avatarUrl = user.profileImageUrl.orEmpty(),
                isFollowing = user.isFollowing,
            )
        }
