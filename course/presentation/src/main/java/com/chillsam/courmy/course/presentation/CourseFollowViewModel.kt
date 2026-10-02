package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 따라가기 화면 ViewModel(FS-21).
 *
 * 코스 데이터는 상세와 같은 BFF 응답([GetCourseDetailUseCase])을 그대로 쓴다 — 따라가기 전용
 * 조회 API 가 없고, 필요한 것(장소 순서·좌표·도보 시간)이 상세 응답에 다 있다.
 *
 * 진행 기록(시작 시각·방문 수)은 화면 세션 상태다. 서버 trace 계약이 아직 없다
 * (swagger 에 course-track 미구현 확인, 2026-10-02).
 * TODO-API-SPEC: trace 계약이 생기면 시작·방문 체크를 서버에 기록한다.
 */
@HiltViewModel
class CourseFollowViewModel
    @Inject
    constructor(
        private val getCourseDetailUseCase: GetCourseDetailUseCase,
    ) : MviViewModel<CourseFollowIntent, CourseFollowUIState, CourseFollowReducerEvent>(
            CourseFollowUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var courseId: Long? = null

        override fun onIntent(intent: CourseFollowIntent) {
            when (intent) {
                is CourseFollowIntent.Load -> {
                    // 같은 코스로 재진입하면 다시 받지 않는다 — 진행 중 상태(방문 기록)를 지키기 위해서다.
                    if (courseId == intent.courseId && uiState.value.detail != null) return
                    courseId = intent.courseId
                    load()
                }

                CourseFollowIntent.Retry -> {
                    load()
                }

                CourseFollowIntent.Start -> {
                    dispatch(CourseFollowReducerEvent.Started(System.currentTimeMillis()))
                }

                CourseFollowIntent.CheckArrival -> {
                    val state = uiState.value
                    val total = state.detail?.places?.size ?: return
                    // 마지막 장소의 방문 체크면 완주 — 종료 시각을 함께 기록한다.
                    val isLast = state.currentIndex >= total - 1
                    dispatch(
                        CourseFollowReducerEvent.Visited(
                            finishedAtMillis = if (isLast) System.currentTimeMillis() else null,
                        ),
                    )
                }

                CourseFollowIntent.ConsumeError -> {
                    dispatch(CourseFollowReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun load() {
            val id = courseId ?: return
            loadJob?.cancel()
            dispatch(CourseFollowReducerEvent.LoadStarted)
            loadJob =
                viewModelScope.launch {
                    runCatching { getCourseDetailUseCase(id) }
                        .onSuccess { detail ->
                            dispatch(CourseFollowReducerEvent.Loaded(detail))
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "따라가기 코스 로드 실패: courseId=$id", e)
                            dispatch(CourseFollowReducerEvent.LoadFailed("코스를 불러오지 못했어요."))
                        }
                }
        }

        override fun reduce(
            state: CourseFollowUIState,
            event: CourseFollowReducerEvent,
        ): CourseFollowUIState =
            when (event) {
                CourseFollowReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is CourseFollowReducerEvent.Loaded -> {
                    state.copy(isLoading = false, detail = event.detail)
                }

                is CourseFollowReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is CourseFollowReducerEvent.Started -> {
                    state.copy(
                        phase = CourseFollowPhase.TRACKING,
                        startedAtMillis = event.startedAtMillis,
                        currentIndex = 0,
                        visitedCount = 0,
                    )
                }

                is CourseFollowReducerEvent.Visited -> {
                    if (event.finishedAtMillis != null) {
                        state.copy(
                            phase = CourseFollowPhase.DONE,
                            visitedCount = state.visitedCount + 1,
                            finishedAtMillis = event.finishedAtMillis,
                        )
                    } else {
                        state.copy(
                            visitedCount = state.visitedCount + 1,
                            currentIndex = state.currentIndex + 1,
                        )
                    }
                }

                CourseFollowReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private companion object {
            const val TAG = "CourseFollow"
        }
    }
