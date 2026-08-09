package com.chillsam.courmy.main.domain.follow

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.my.FollowUserVO

/**
 * 팔로워·팔로잉 목록 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * [userId] 가 null 이면 **내** 목록이다 — data 레이어가 세션(JWT)에서 id 를 꺼내 쓴다.
 * 값을 주면 그 사용자의 목록을 본다(서버 `GET /api/v1/users/{userId}/followers|followings`).
 */
interface FollowListRepository {
    /** [cursor] 가 null 이면 첫 페이지, 아니면 그 커서 다음부터 이어 받는다. */
    suspend fun getFollowers(
        userId: Long? = null,
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<FollowUserVO>

    suspend fun getFollowings(
        userId: Long? = null,
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<FollowUserVO>
}
