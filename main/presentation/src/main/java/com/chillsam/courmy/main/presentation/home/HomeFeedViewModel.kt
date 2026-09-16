package com.chillsam.courmy.main.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.main.domain.home.GetHomeFeedUseCase
import com.chillsam.courmy.main.domain.saved.GetSavedCourseIdsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 홈 공개 코스 피드(FS-09) ViewModel. `GET /service/v1/courses` 를 [GetHomeFeedUseCase] 로 로드한다.
 *
 * 공개 엔드포인트라 로그인 여부와 무관하게 호출한다(게스트 홈에서도 피드가 보인다).
 *
 * 생성 시점에는 로드하지 않는다. 화면이 RefreshOnResume 으로 부르므로,
 * init 에서도 부르면 진입할 때 같은 요청이 두 번 나간다.
 */
@HiltViewModel
class HomeFeedViewModel
    @Inject
    constructor(
        private val getHomeFeedUseCase: GetHomeFeedUseCase,
        private val setCourseSavedUseCase: SetCourseSavedUseCase,
        private val getSavedCourseIdsUseCase: GetSavedCourseIdsUseCase,
    ) : MviViewModel<HomeFeedIntent, HomeFeedUIState, HomeFeedReducerEvent>(
            HomeFeedUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var moreJob: Job? = null

        override fun onIntent(intent: HomeFeedIntent) {
            when (intent) {
                HomeFeedIntent.Load,
                HomeFeedIntent.Retry,
                -> load()

                HomeFeedIntent.LoadMore -> loadMore()

                is HomeFeedIntent.ToggleSave -> toggleSave(intent.courseId)

                HomeFeedIntent.ConsumeError -> dispatch(HomeFeedReducerEvent.ErrorConsumed)
            }
        }

        override fun reduce(
            state: HomeFeedUIState,
            event: HomeFeedReducerEvent,
        ): HomeFeedUIState =
            when (event) {
                HomeFeedReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is HomeFeedReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        courses = event.courses.toImmutableList(),
                        savedCourseIds = event.savedCourseIds.toImmutableSet(),
                        errorMessage = null,
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                HomeFeedReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is HomeFeedReducerEvent.MoreLoaded -> {
                    state.copy(
                        // 서버가 같은 코스를 다시 줘도 화면에 두 번 그리지 않는다(LazyColumn key 중복 크래시 방지).
                        courses = (state.courses + event.courses).distinctBy { it.id }.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                is HomeFeedReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false, actionErrorMessage = event.message)
                }

                is HomeFeedReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is HomeFeedReducerEvent.SaveStarted -> {
                    state.copy(savingCourseIds = (state.savingCourseIds + event.courseId).toImmutableSet())
                }

                is HomeFeedReducerEvent.SaveFinished -> {
                    val saved =
                        if (event.saved) {
                            state.savedCourseIds + event.courseId
                        } else {
                            state.savedCourseIds - event.courseId
                        }
                    state.copy(
                        savedCourseIds = saved.toImmutableSet(),
                        savingCourseIds = (state.savingCourseIds - event.courseId).toImmutableSet(),
                    )
                }

                is HomeFeedReducerEvent.SaveFailed -> {
                    state.copy(
                        savingCourseIds = (state.savingCourseIds - event.courseId).toImmutableSet(),
                        actionErrorMessage = event.message,
                    )
                }

                HomeFeedReducerEvent.ErrorConsumed -> {
                    state.copy(actionErrorMessage = null)
                }
            }

        /** 서버가 확정한 뒤에 아이콘을 바꾼다. 진행 중인 같은 코스의 중복 탭은 무시한다. */
        private fun toggleSave(courseId: String) {
            val state = currentState
            if (courseId in state.savingCourseIds) return
            val id = courseId.toLongOrNull()
            if (id == null) {
                Log.w(TAG, "잘못된 코스 id: $courseId")
                return
            }
            val target = courseId !in state.savedCourseIds
            dispatch(HomeFeedReducerEvent.SaveStarted(courseId))
            viewModelScope.launch {
                runCatching { setCourseSavedUseCase(courseId = id, saved = target) }
                    .onSuccess { dispatch(HomeFeedReducerEvent.SaveFinished(courseId, target)) }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "코스 저장 토글 실패: courseId=$courseId, target=$target", e)
                        val message = if (target) "저장하지 못했어요." else "저장을 취소하지 못했어요."
                        dispatch(HomeFeedReducerEvent.SaveFailed(courseId, message))
                    }
            }
        }

        private fun load() {
            dispatch(HomeFeedReducerEvent.LoadStarted)
            loadJob?.cancel()
            // 진행 중인 이어받기를 끊는다. 안 끊으면 뒤늦게 도착한 옛 페이지가 새 목록 뒤에 붙는다.
            moreJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getHomeFeedUseCase() }
                        .onSuccess { page ->
                            dispatch(
                                HomeFeedReducerEvent.Loaded(
                                    courses = page.items,
                                    savedCourseIds = loadSavedCourseIds(),
                                    nextCursor = page.nextCursor,
                                    hasNext = page.hasNext,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "코스 피드 로드 실패", e)
                            dispatch(HomeFeedReducerEvent.Failed("코스를 불러오지 못했습니다."))
                        }
                }
        }

        /**
         * 다음 페이지를 이어 받는다.
         *
         * 첫 로드가 끝나기 전이거나(커서 없음) 마지막 페이지면 아무것도 하지 않는다.
         * 스크롤 위치가 조금만 흔들려도 호출되므로 중복 요청을 여기서 막는다 — [loadJob] 을 취소하지 않는 이유는
         * 첫 로드 중에는 애초에 진입하지 않기 때문이다.
         */
        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore) return
            // 마지막 페이지면 커서가 없다. 둘 중 하나만 봐도 되지만 서버가 어긋나게 줄 때를 대비해 함께 본다.
            val cursor = state.nextCursor?.takeIf { state.hasNext } ?: return
            dispatch(HomeFeedReducerEvent.LoadMoreStarted)
            moreJob?.cancel()
            moreJob =
                viewModelScope.launch {
                    runCatching { getHomeFeedUseCase(cursor = cursor) }
                        .onSuccess { page ->
                            dispatch(
                                HomeFeedReducerEvent.MoreLoaded(
                                    courses = page.items,
                                    nextCursor = page.nextCursor,
                                    hasNext = page.hasNext,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "코스 피드 다음 페이지 로드 실패", e)
                            dispatch(HomeFeedReducerEvent.MoreFailed("더 불러오지 못했어요."))
                        }
                }
        }

        /**
         * 저장 여부 표시용 id 집합. 실패해도 피드는 그대로 보여준다(부가 정보이므로).
         * 비로그인이면 401 이라 빈 집합이 되고, 카드는 모두 저장 전 아이콘으로 그려진다.
         */
        private suspend fun loadSavedCourseIds(): Set<String> =
            runCatching { getSavedCourseIdsUseCase() }
                .getOrElse { e ->
                    if (e is CancellationException) throw e
                    Log.d(TAG, "저장 코스 id 조회 실패(피드는 그대로 표시): ${e.message}")
                    emptySet()
                }

        private companion object {
            const val TAG = "HomeFeed"
        }
    }
