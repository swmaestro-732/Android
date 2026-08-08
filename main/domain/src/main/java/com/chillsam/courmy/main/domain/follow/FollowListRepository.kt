package com.chillsam.courmy.main.domain.follow

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.my.FollowUserVO

/**
 * 팔로워·팔로잉 목록 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 지금은 **내** 목록만 본다. 대상 사용자 id 는 data 레이어가 세션(JWT)에서 꺼내 쓴다.
 * TODO-API-SPEC: 타유저 프로필에서 그 사람의 목록을 보게 되면 userId 파라미터를 연다. [wiki-needed]
 */
interface FollowListRepository {
    /** [cursor] 가 null 이면 첫 페이지, 아니면 그 커서 다음부터 이어 받는다. */
    suspend fun getMyFollowers(
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<FollowUserVO>

    suspend fun getMyFollowings(
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<FollowUserVO>
}
