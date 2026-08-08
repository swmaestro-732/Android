package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.my.FollowUserVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 팔로우 목록 상태. 탭마다 결과를 따로 들고 있어 오갈 때 다시 부르지 않는다.
 * [loadedTabs] 에 없는 탭을 고르면 그때 조회한다.
 */
data class FollowListUIState(
    val selectedTab: FollowTab = FollowTab.FOLLOWER,
    val followers: ImmutableList<FollowUserVO> = persistentListOf(),
    val followings: ImmutableList<FollowUserVO> = persistentListOf(),
    val loadedTabs: Set<FollowTab> = emptySet(),
    val isLoading: Boolean = false,
    val loadErrorMessage: String? = null,
    val errorMessage: String? = null,
    /** 탭마다 목록을 따로 들고 있으므로 커서도 탭별로 나눠 둔다. */
    val followerCursor: String? = null,
    val followerHasNext: Boolean = false,
    val followingCursor: String? = null,
    val followingHasNext: Boolean = false,
    val isLoadingMore: Boolean = false,
) : UiState {
    val users: ImmutableList<FollowUserVO>
        get() = if (selectedTab == FollowTab.FOLLOWER) followers else followings

    /** 지금 탭의 다음 페이지 커서. 더 없으면 null. */
    val currentCursor: String?
        get() =
            if (selectedTab == FollowTab.FOLLOWER) {
                followerCursor?.takeIf { followerHasNext }
            } else {
                followingCursor?.takeIf { followingHasNext }
            }

    /** [tab] 에 해당하는 목록. */
    fun usersOf(tab: FollowTab): ImmutableList<FollowUserVO> = if (tab == FollowTab.FOLLOWER) followers else followings

    /**
     * [tab] 자리에 목록과 커서를 채워 돌려준다.
     *
     * 탭마다 목록·커서·hasNext 로 필드가 3개씩이라, 호출부에서 매번 갈라 쓰면 reduce 가 금방 길어진다.
     */
    fun withPage(
        tab: FollowTab,
        users: ImmutableList<FollowUserVO>,
        nextCursor: String?,
        hasNext: Boolean,
    ): FollowListUIState =
        if (tab == FollowTab.FOLLOWER) {
            copy(followers = users, followerCursor = nextCursor, followerHasNext = hasNext)
        } else {
            copy(followings = users, followingCursor = nextCursor, followingHasNext = hasNext)
        }

    companion object {
        val empty: FollowListUIState = FollowListUIState()
    }
}
