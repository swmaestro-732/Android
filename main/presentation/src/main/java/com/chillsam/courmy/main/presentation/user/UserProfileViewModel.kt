package com.chillsam.courmy.main.presentation.user

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.auth.IsLoggedInUseCase
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.user.GetUserProfileUseCase
import com.chillsam.courmy.main.domain.user.ToggleFollowUseCase
import com.chillsam.courmy.main.entity.user.FollowRelation
import com.chillsam.courmy.main.entity.user.UserProfileVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 타유저 프로필(FS-15 OtherUserPage) ViewModel.
 * `GET /service/v1/mypage/{handle}` 을 [GetUserProfileUseCase] 로 로드하고,
 * 팔로우 버튼은 [ToggleFollowUseCase] 로 현재 관계의 반대를 요청한다.
 *
 * 조회 키(handle)는 라우트 인자라 [UserProfileIntent.Load] 로 받아 보관하고, 재시도 때 재사용한다.
 */
@HiltViewModel
class UserProfileViewModel
    @Inject
    constructor(
        private val getUserProfileUseCase: GetUserProfileUseCase,
        private val toggleFollowUseCase: ToggleFollowUseCase,
        private val isLoggedInUseCase: IsLoggedInUseCase,
    ) : MviViewModel<UserProfileIntent, UserProfileUIState, UserProfileReducerEvent>(
            UserProfileUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var moreJob: Job? = null
        private var followJob: Job? = null
        private var handle: String = ""

        override fun onIntent(intent: UserProfileIntent) {
            when (intent) {
                is UserProfileIntent.Load -> {
                    handle = intent.handle
                    load()
                }

                UserProfileIntent.Retry -> {
                    load()
                }

                UserProfileIntent.LoadMore -> {
                    loadMore()
                }

                UserProfileIntent.ToggleFollow -> {
                    toggleFollow()
                }

                UserProfileIntent.ConsumeFollowError -> {
                    dispatch(UserProfileReducerEvent.FollowErrorConsumed)
                }

                UserProfileIntent.ConsumeLoginRequired -> {
                    dispatch(UserProfileReducerEvent.LoginRequiredConsumed)
                }
            }
        }

        override fun reduce(
            state: UserProfileUIState,
            event: UserProfileReducerEvent,
        ): UserProfileUIState =
            when (event) {
                UserProfileReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is UserProfileReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        profile = event.profile,
                        errorMessage = null,
                        courses =
                            event.profile.courses.items
                                .toImmutableList(),
                        nextCursor = event.profile.courses.nextCursor,
                        hasNext = event.profile.courses.hasNext,
                        isLoadingMore = false,
                    )
                }

                is UserProfileReducerEvent.Failed -> {
                    // 실패 시 이전 프로필을 비워, stale 데이터가 에러 화면을 가리지 않게 한다.
                    state.copy(isLoading = false, profile = null, errorMessage = event.message)
                }

                UserProfileReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is UserProfileReducerEvent.MoreLoaded -> {
                    state.copy(
                        // 서버가 같은 코스를 다시 줘도 두 번 그리지 않는다.
                        courses = (state.courses + event.courses).distinctBy { it.id }.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                UserProfileReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false)
                }

                else -> {
                    reduceFollow(state, event)
                }
            }

        private fun reduceFollow(
            state: UserProfileUIState,
            event: UserProfileReducerEvent,
        ): UserProfileUIState =
            when (event) {
                UserProfileReducerEvent.FollowStarted -> {
                    state.copy(isFollowInFlight = true, followErrorMessage = null)
                }

                is UserProfileReducerEvent.FollowUpdated -> {
                    state.copy(
                        isFollowInFlight = false,
                        profile =
                            state.profile?.copy(
                                relation = event.relation,
                                followerCount = event.followerCount,
                            ),
                    )
                }

                is UserProfileReducerEvent.FollowFailed -> {
                    state.copy(isFollowInFlight = false, followErrorMessage = event.message)
                }

                UserProfileReducerEvent.FollowErrorConsumed -> {
                    state.copy(followErrorMessage = null)
                }

                UserProfileReducerEvent.LoginRequired -> {
                    state.copy(needsLogin = true)
                }

                UserProfileReducerEvent.LoginRequiredConsumed -> {
                    state.copy(needsLogin = false)
                }

                else -> {
                    state
                }
            }

        private fun load() {
            // handle 검증이 더미 분기보다 먼저다. 라우트 인자가 빠지면 빈 handle 이 들어오는데,
            // 순서가 반대면 debug 빌드에서 handle 이 빈 더미 프로필을 정상 화면처럼 렌더한다.
            if (handle.isBlank()) {
                dispatch(UserProfileReducerEvent.Failed("사용자를 찾을 수 없습니다."))
                return
            }
            dispatch(UserProfileReducerEvent.LoadStarted)
            loadJob?.cancel()
            // 진행 중인 이어받기를 끊는다. 안 끊으면 뒤늦게 도착한 옛 페이지가 새 목록 뒤에 붙는다.
            moreJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getUserProfileUseCase(handle) }
                        .onSuccess { profile -> dispatch(UserProfileReducerEvent.Loaded(profile)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "타유저 프로필 로드 실패: handle=$handle", e)
                            dispatch(UserProfileReducerEvent.Failed("프로필을 불러오지 못했습니다."))
                        }
                }
        }

        /**
         * 다음 페이지를 이어 받는다. 첫 로드 중이거나 마지막 페이지면 아무것도 하지 않는다.
         * 스크롤이 조금만 흔들려도 호출되므로 중복 요청을 여기서 막는다.
         */
        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore) return
            val cursor = state.nextCursor?.takeIf { state.hasNext } ?: return
            dispatch(UserProfileReducerEvent.LoadMoreStarted)
            moreJob?.cancel()
            moreJob =
                viewModelScope.launch {
                    runCatching { getUserProfileUseCase(handle = handle, cursor = cursor) }
                        .onSuccess { profile ->
                            dispatch(
                                UserProfileReducerEvent.MoreLoaded(
                                    courses = profile.courses.items,
                                    nextCursor = profile.courses.nextCursor,
                                    hasNext = profile.courses.hasNext,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "타유저 코스 다음 페이지 로드 실패: handle=$handle", e)
                            dispatch(UserProfileReducerEvent.MoreFailed)
                        }
                }
        }

        private fun toggleFollow() {
            val state = currentState
            val profile = state.profile
            // 자기 자신에게는 버튼을 노출하지 않지만, 중복 탭·경합으로 들어오는 경우를 막는다.
            if (profile == null || profile.isMe || state.isFollowInFlight) return
            // 비로그인이면 요청을 보내지 않는다 — 401 을 "팔로우하지 못했어요"로 알리면 이유를 알 수 없다.
            if (isLoggedInUseCase()) {
                requestFollow(profile)
            } else {
                dispatch(UserProfileReducerEvent.LoginRequired)
            }
        }

        private fun requestFollow(profile: UserProfileVO) {
            dispatch(UserProfileReducerEvent.FollowStarted)
            followJob?.cancel()
            followJob =
                viewModelScope.launch {
                    runCatching {
                        toggleFollowUseCase(
                            userId = profile.id,
                            currentlyFollowing = profile.relation.isFollowing,
                        )
                    }.onSuccess { result ->
                        dispatch(
                            UserProfileReducerEvent.FollowUpdated(
                                // 관계는 서버가 확정한 isFollowing 으로 다시 만든다.
                                // 상대가 나를 팔로우하는지는 이번 요청으로 바뀌지 않으므로 기존 값을 유지한다.
                                relation =
                                    FollowRelation.of(
                                        isFollowing = result.isFollowing,
                                        isFollower = profile.relation.isFollower,
                                    ),
                                followerCount = result.followerCount,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "팔로우 토글 실패: userId=${profile.id}", e)
                        dispatch(UserProfileReducerEvent.FollowFailed("잠시 후 다시 시도해 주세요."))
                    }
                }
        }

        companion object {
            private const val TAG = "UserProfileViewModel"
        }
    }
