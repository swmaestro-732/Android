package com.chillsam.courmy.main.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.main.domain.home.GetHomeFeedUseCase
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
 */
@HiltViewModel
class HomeFeedViewModel
    @Inject
    constructor(
        private val getHomeFeedUseCase: GetHomeFeedUseCase,
        private val setCourseSavedUseCase: SetCourseSavedUseCase,
    ) : MviViewModel<HomeFeedIntent, HomeFeedUIState, HomeFeedReducerEvent>(
            HomeFeedUIState.empty,
        ) {
        private var loadJob: Job? = null

        init {
            onIntent(HomeFeedIntent.Load)
        }

        override fun onIntent(intent: HomeFeedIntent) {
            when (intent) {
                HomeFeedIntent.Load,
                HomeFeedIntent.Retry,
                -> load()

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
                        errorMessage = null,
                    )
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
            loadJob =
                viewModelScope.launch {
                    runCatching { getHomeFeedUseCase() }
                        .onSuccess { courses -> dispatch(HomeFeedReducerEvent.Loaded(courses)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "코스 피드 로드 실패", e)
                            dispatch(HomeFeedReducerEvent.Failed("코스를 불러오지 못했습니다."))
                        }
                }
        }

        private companion object {
            const val TAG = "HomeFeed"
        }
    }
