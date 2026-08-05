package com.chillsam.courmy.main.presentation.saved

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.main.domain.saved.GetSavedCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 저장함 · 코스 탭(FS-14) ViewModel. `GET /service/v1/my/saved-courses` 로 목록을 로드하고,
 * 저장 취소는 코스 상세·홈 피드와 같은 [SetCourseSavedUseCase] 를 쓴다(중복 구현 방지).
 *
 * 로그인 사용자만 진입하는 화면이라(게스트는 별도 화면) JWT 가 있다고 전제한다.
 *
 * 생성 시점에는 로드하지 않는다. 화면이 RefreshOnResume 으로 부르므로,
 * init 에서도 부르면 진입할 때 같은 요청이 두 번 나간다.
 */
@HiltViewModel
class SavedCoursesViewModel
    @Inject
    constructor(
        private val getSavedCoursesUseCase: GetSavedCoursesUseCase,
        private val setCourseSavedUseCase: SetCourseSavedUseCase,
    ) : MviViewModel<SavedCoursesIntent, SavedCoursesUIState, SavedCoursesReducerEvent>(
            SavedCoursesUIState.empty,
        ) {
        private var loadJob: Job? = null
        private val unsaveJobs = mutableMapOf<String, Job>()

        override fun onIntent(intent: SavedCoursesIntent) {
            when (intent) {
                SavedCoursesIntent.Load,
                SavedCoursesIntent.Retry,
                -> {
                    load()
                }

                is SavedCoursesIntent.Unsave -> {
                    unsave(intent.courseId)
                }

                SavedCoursesIntent.ConsumeError -> {
                    dispatch(SavedCoursesReducerEvent.ErrorConsumed)
                }
            }
        }

        override fun reduce(
            state: SavedCoursesUIState,
            event: SavedCoursesReducerEvent,
        ): SavedCoursesUIState =
            when (event) {
                SavedCoursesReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, loadErrorMessage = null)
                }

                is SavedCoursesReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        courses = event.courses.toImmutableList(),
                        loadErrorMessage = null,
                    )
                }

                is SavedCoursesReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadErrorMessage = event.message)
                }

                is SavedCoursesReducerEvent.Unsaved -> {
                    state.copy(
                        courses = state.courses.filterNot { it.id == event.courseId }.toImmutableList(),
                    )
                }

                is SavedCoursesReducerEvent.UnsaveFailed -> {
                    state.copy(errorMessage = event.message)
                }

                SavedCoursesReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private fun load() {
            dispatch(SavedCoursesReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getSavedCoursesUseCase() }
                        .onSuccess { courses -> dispatch(SavedCoursesReducerEvent.Loaded(courses)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "저장 코스 로드 실패", e)
                            dispatch(SavedCoursesReducerEvent.LoadFailed("저장한 코스를 불러오지 못했습니다."))
                        }
                }
        }

        /**
         * 서버가 성공을 확인한 뒤에만 목록에서 뺀다.
         * 요청은 코스마다 독립이라 하나의 job 으로 묶어 취소하면, 앞선 취소 요청이 서버에는 반영되고
         * 화면에는 남는 불일치가 생긴다.
         * 먼저 지우고 실패 시 되돌리는 방식은 실패가 잦은 초기 연동에서 화면이 튀어 보이기 쉽다.
         */
        private fun unsave(courseId: String) {
            val id = courseId.toLongOrNull()
            if (id == null) {
                Log.w(TAG, "잘못된 코스 id: $courseId")
                dispatch(SavedCoursesReducerEvent.UnsaveFailed("저장을 취소하지 못했어요."))
                return
            }
            // 코스마다 독립된 요청이라 서로 취소하지 않는다. 같은 코스의 중복 탭만 무시한다.
            if (unsaveJobs[courseId]?.isActive == true) return
            unsaveJobs[courseId] =
                viewModelScope.launch {
                    try {
                        runCatching { setCourseSavedUseCase(courseId = id, saved = false) }
                            .onSuccess { dispatch(SavedCoursesReducerEvent.Unsaved(courseId)) }
                            .onFailure { e ->
                                if (e is CancellationException) throw e
                                Log.w(TAG, "저장 취소 실패: courseId=$courseId", e)
                                dispatch(SavedCoursesReducerEvent.UnsaveFailed("저장을 취소하지 못했어요."))
                            }
                    } finally {
                        unsaveJobs.remove(courseId)
                    }
                }
        }

        private companion object {
            const val TAG = "SavedCourses"
        }
    }
