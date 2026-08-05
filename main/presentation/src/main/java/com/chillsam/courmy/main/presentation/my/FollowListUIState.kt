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
) : UiState {
    val users: ImmutableList<FollowUserVO>
        get() = if (selectedTab == FollowTab.FOLLOWER) followers else followings

    companion object {
        val empty: FollowListUIState = FollowListUIState()
    }
}
