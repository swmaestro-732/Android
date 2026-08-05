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
        private val unfollowJobs = mutableMapOf<Long, Job>()

        override fun onIntent(intent: FollowListIntent) {
            when (intent) {
                is FollowListIntent.SelectTab -> {
                    dispatch(FollowListReducerEvent.TabSelected(intent.tab))
                    // 이미 불러온 탭은 다시 부르지 않는다(탭을 오갈 때마다 요청이 나가지 않게).
                    if (intent.tab !in currentState.loadedTabs) load(intent.tab)
                }

                FollowListIntent.Retry -> {
                    load(currentState.selectedTab)
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
                    val users = event.users.toImmutableList()
                    state.copy(
                        isLoading = false,
                        followers = if (event.tab == FollowTab.FOLLOWER) users else state.followers,
                        followings = if (event.tab == FollowTab.FOLLOWING) users else state.followings,
                        loadedTabs = state.loadedTabs + event.tab,
                        loadErrorMessage = null,
                    )
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
            loadJob =
                viewModelScope.launch {
                    runCatching { getFollowListUseCase(followers = tab == FollowTab.FOLLOWER) }
                        .onSuccess { users -> dispatch(FollowListReducerEvent.Loaded(tab, users)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "팔로우 목록 로드 실패: tab=$tab", e)
                            dispatch(FollowListReducerEvent.LoadFailed("목록을 불러오지 못했습니다."))
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
