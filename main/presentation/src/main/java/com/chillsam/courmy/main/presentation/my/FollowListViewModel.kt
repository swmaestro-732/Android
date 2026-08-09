package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.follow.GetFollowListUseCase
import com.chillsam.courmy.main.domain.user.ToggleFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 팔로우 목록(FS-15 O) ViewModel.
 * `GET /api/v1/users/{userId}/followers`·`/followings` 로 내 목록을 불러온다.
 *
 * 팔로잉 해제는 [ToggleFollowUseCase] 로 보낸다.
 * 팔로워 "삭제"는 서버에 대응 동작이 없다 — `DELETE /followers/{userId}` 는 "내가 그 사람을
 * 언팔로우"라 의미가 반대다. 그래서 이 ViewModel 은 팔로워 탭의 제거를 받지 않는다.
 */
@HiltViewModel
class FollowListViewModel
    @Inject
    constructor(
        private val getFollowListUseCase: GetFollowListUseCase,
        private val toggleFollowUseCase: ToggleFollowUseCase,
    ) : MviViewModel<FollowListIntent, FollowListUIState, FollowListReducerEvent>(
            FollowListUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var moreJob: Job? = null
        private val unfollowJobs = mutableMapOf<Long, Job>()

        /** 조회 대상. null 이면 내 목록(서버가 JWT 로 식별). 타유저 프로필에서 넘어올 때만 채워진다. */
        private var targetUserId: Long? = null

        override fun onIntent(intent: FollowListIntent) {
            when (intent) {
                is FollowListIntent.SetTarget -> {
                    targetUserId = intent.userId
                }

                is FollowListIntent.SelectTab -> {
                    dispatch(FollowListReducerEvent.TabSelected(intent.tab))
                    // 이미 불러온 탭은 다시 부르지 않는다(탭을 오갈 때마다 요청이 나가지 않게).
                    if (intent.tab !in currentState.loadedTabs) load(intent.tab)
                }

                FollowListIntent.Retry -> {
                    load(currentState.selectedTab)
                }

                FollowListIntent.LoadMore -> {
                    loadMore()
                }

                is FollowListIntent.Unfollow -> {
                    unfollow(intent.userId)
                }

                FollowListIntent.ConsumeError -> {
                    dispatch(FollowListReducerEvent.ErrorConsumed)
                }
            }
        }

        override fun reduce(
            state: FollowListUIState,
            event: FollowListReducerEvent,
        ): FollowListUIState =
            when (event) {
                is FollowListReducerEvent.TabSelected -> {
                    state.copy(selectedTab = event.tab, loadErrorMessage = null)
                }

                FollowListReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, loadErrorMessage = null)
                }

                is FollowListReducerEvent.Loaded -> {
                    state
                        .withPage(event.tab, event.users.toImmutableList(), event.nextCursor, event.hasNext)
                        .copy(
                            isLoading = false,
                            loadedTabs = state.loadedTabs + event.tab,
                            loadErrorMessage = null,
                            isLoadingMore = false,
                        )
                }

                FollowListReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is FollowListReducerEvent.MoreLoaded -> {
                    val current = state.usersOf(event.tab)
                    // 서버가 같은 사용자를 다시 줘도 두 번 그리지 않는다(LazyColumn key 중복 방지).
                    val merged = (current + event.users).distinctBy { it.id }.toImmutableList()
                    state
                        .withPage(event.tab, merged, event.nextCursor, event.hasNext)
                        .copy(isLoadingMore = false)
                }

                is FollowListReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false, errorMessage = event.message)
                }

                is FollowListReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadErrorMessage = event.message)
                }

                is FollowListReducerEvent.Unfollowed -> {
                    state.copy(
                        followings = state.followings.filterNot { it.id == event.userId }.toImmutableList(),
                        // 팔로워 목록에도 같은 사람이 있으면 관계 표시를 맞춰 준다.
                        followers =
                            state.followers
                                .map { if (it.id == event.userId) it.copy(isFollowing = false) else it }
                                .toImmutableList(),
                    )
                }

                is FollowListReducerEvent.ActionFailed -> {
                    state.copy(errorMessage = event.message)
                }

                FollowListReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private fun load(tab: FollowTab) {
            dispatch(FollowListReducerEvent.LoadStarted)
            loadJob?.cancel()
            // 진행 중인 이어받기를 끊는다. 안 끊으면 뒤늦게 도착한 옛 페이지가 새 목록 뒤에 붙는다.
            moreJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching {
                        getFollowListUseCase(followers = tab == FollowTab.FOLLOWER, userId = targetUserId)
                    }.onSuccess { page ->
                        dispatch(
                            FollowListReducerEvent.Loaded(
                                tab = tab,
                                users = page.items,
                                nextCursor = page.nextCursor,
                                hasNext = page.hasNext,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                        Log.w(TAG, "팔로우 목록 로드 실패: tab=$tab", e)
                        dispatch(FollowListReducerEvent.LoadFailed("목록을 불러오지 못했습니다."))
                    }
                }
        }

        /** 지금 탭의 다음 페이지를 이어 받는다. 첫 로드 전이거나 마지막 페이지면 아무것도 하지 않는다. */
        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore) return
            val cursor = state.currentCursor ?: return
            val tab = state.selectedTab
            dispatch(FollowListReducerEvent.LoadMoreStarted)
            moreJob?.cancel()
            moreJob =
                viewModelScope.launch {
                    runCatching {
                        getFollowListUseCase(
                            followers = tab == FollowTab.FOLLOWER,
                            userId = targetUserId,
                            cursor = cursor,
                        )
                    }.onSuccess { page ->
                        dispatch(
                            FollowListReducerEvent.MoreLoaded(
                                tab = tab,
                                users = page.items,
                                nextCursor = page.nextCursor,
                                hasNext = page.hasNext,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "팔로우 목록 다음 페이지 로드 실패: tab=$tab", e)
                        dispatch(FollowListReducerEvent.MoreFailed("더 불러오지 못했어요."))
                    }
                }
        }

        /**
         * 서버가 확정한 뒤에 목록에서 뺀다(먼저 지우고 실패 시 되돌리면 목록이 튄다).
         * 사용자마다 독립된 요청이라 서로 취소하지 않는다 — 하나로 묶으면 앞선 해제가 서버에는
         * 반영되고 화면에는 남는다. 같은 사용자의 중복 탭만 무시한다.
         */
        private fun unfollow(userId: Long) {
            if (unfollowJobs[userId]?.isActive == true) return
            unfollowJobs[userId] =
                viewModelScope.launch {
                    try {
                        runCatching { toggleFollowUseCase(userId = userId, currentlyFollowing = true) }
                            .onSuccess { dispatch(FollowListReducerEvent.Unfollowed(userId)) }
                            .onFailure { e ->
                                if (e is CancellationException) throw e
                                Log.w(TAG, "팔로잉 해제 실패: userId=$userId", e)
                                dispatch(FollowListReducerEvent.ActionFailed("팔로잉을 해제하지 못했어요."))
                            }
                    } finally {
                        unfollowJobs.remove(userId)
                    }
                }
        }

        private companion object {
            const val TAG = "FollowList"
        }
    }
