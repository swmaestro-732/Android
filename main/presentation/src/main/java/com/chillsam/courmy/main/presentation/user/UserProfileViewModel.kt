package com.chillsam.courmy.main.presentation.user

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.user.GetUserProfileUseCase
import com.chillsam.courmy.main.domain.user.ToggleFollowUseCase
import com.chillsam.courmy.main.entity.user.FollowRelation
import dagger.hilt.android.lifecycle.HiltViewModel
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
    ) : MviViewModel<UserProfileIntent, UserProfileUIState, UserProfileReducerEvent>(
            UserProfileUIState.empty,
        ) {
        private var loadJob: Job? = null
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

                UserProfileIntent.ToggleFollow -> {
                    toggleFollow()
                }

                UserProfileIntent.ConsumeFollowError -> {
                    dispatch(UserProfileReducerEvent.FollowErrorConsumed)
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
                    state.copy(isLoading = false, profile = event.profile, errorMessage = null)
                }

                is UserProfileReducerEvent.Failed -> {
                    // 실패 시 이전 프로필을 비워, stale 데이터가 에러 화면을 가리지 않게 한다.
                    state.copy(isLoading = false, profile = null, errorMessage = event.message)
                }

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
            }

        private fun load() {
            if (handle.isBlank()) {
                dispatch(UserProfileReducerEvent.Failed("사용자를 찾을 수 없습니다."))
                return
            }
            dispatch(UserProfileReducerEvent.LoadStarted)
            loadJob?.cancel()
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

        private fun toggleFollow() {
            val profile = uiState.value.profile ?: return
            // 자기 자신에게는 버튼을 노출하지 않지만, 중복 탭·경합으로 들어오는 경우를 막는다.
            if (profile.isMe || uiState.value.isFollowInFlight) return

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
