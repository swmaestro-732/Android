package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 마이·프로필(FS-15) ViewModel. `GET /service/v1/mypage`(BFF)를 [GetMyProfileUseCase] 로 로드하고
 * 로딩/성공/실패를 [MyProfileUIState] 로 노출한다(에러 시 [MyProfileIntent.Retry]).
 *
 * 코스 목록은 커서 페이징이라 페이지를 이어 붙여 [MyProfileUIState.courses] 에 누적한다.
 * 서버에 "내 코스만" 주는 API 가 없어 다음 페이지 요청에도 프로필이 함께 오는데, 프로필은 첫 로드
 * 값을 유지하고 코스만 붙인다(스크롤 중에 헤더 숫자가 흔들리지 않게).
 *
 * 생성 시점에는 로드하지 않는다. 화면이 RefreshOnResume 으로 부르므로,
 * init 에서도 부르면 진입할 때 같은 요청이 두 번 나간다.
 */
@HiltViewModel
class MyViewModel
    @Inject
    constructor(
        private val getMyProfileUseCase: GetMyProfileUseCase,
    ) : MviViewModel<MyProfileIntent, MyProfileUIState, MyProfileReducerEvent>(
            MyProfileUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var moreJob: Job? = null

        override fun onIntent(intent: MyProfileIntent) {
            when (intent) {
                MyProfileIntent.Load,
                MyProfileIntent.Retry,
                -> load()

                MyProfileIntent.LoadMore -> loadMore()
            }
        }

        override fun reduce(
            state: MyProfileUIState,
            event: MyProfileReducerEvent,
        ): MyProfileUIState =
            when (event) {
                MyProfileReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is MyProfileReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        profile = event.profile,
                        errorMessage = null,
                        courses =
                            event.profile.myCourses.items
                                .toImmutableList(),
                        nextCursor = event.profile.myCourses.nextCursor,
                        hasNext = event.profile.myCourses.hasNext,
                        isLoadingMore = false,
                    )
                }

                is MyProfileReducerEvent.Failed -> {
                    // 실패 시 이전 프로필을 비워, stale 데이터가 에러 화면을 가리지 않게 한다.
                    state.copy(isLoading = false, profile = null, errorMessage = event.message)
                }

                MyProfileReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is MyProfileReducerEvent.MoreLoaded -> {
                    state.copy(
                        // 서버가 같은 코스를 다시 줘도 두 번 그리지 않는다.
                        courses = (state.courses + event.courses).distinctBy { it.id }.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                MyProfileReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false)
                }
            }

        private fun load() {
            dispatch(MyProfileReducerEvent.LoadStarted)
            loadJob?.cancel()
            // 진행 중인 이어받기를 끊는다. 안 끊으면 뒤늦게 도착한 옛 페이지가 새 목록 뒤에 붙는다.
            moreJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getMyProfileUseCase() }
                        .onSuccess { profile -> dispatch(MyProfileReducerEvent.Loaded(profile)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "마이 프로필 로드 실패", e)
                            dispatch(MyProfileReducerEvent.Failed("프로필을 불러오지 못했습니다."))
                        }
                }
        }

        /**
         * 다음 페이지를 이어 받는다.
         *
         * 첫 로드가 끝나기 전이거나 마지막 페이지면 아무것도 하지 않는다. 스크롤이 조금만 흔들려도
         * 호출되므로 중복 요청을 여기서 막는다.
         */
        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore) return
            // 마지막 페이지면 커서가 없다. 서버가 어긋나게 줄 때를 대비해 둘을 함께 본다.
            val cursor = state.nextCursor?.takeIf { state.hasNext } ?: return
            dispatch(MyProfileReducerEvent.LoadMoreStarted)
            moreJob?.cancel()
            moreJob =
                viewModelScope.launch {
                    runCatching { getMyProfileUseCase(cursor = cursor) }
                        .onSuccess { profile ->
                            dispatch(
                                MyProfileReducerEvent.MoreLoaded(
                                    courses = profile.myCourses.items,
                                    nextCursor = profile.myCourses.nextCursor,
                                    hasNext = profile.myCourses.hasNext,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "내 코스 다음 페이지 로드 실패", e)
                            dispatch(MyProfileReducerEvent.MoreFailed)
                        }
                }
        }

        companion object {
            private const val TAG = "MyViewModel"
        }
    }
