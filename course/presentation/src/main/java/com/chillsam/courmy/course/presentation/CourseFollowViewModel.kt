package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CheckInTracePlaceUseCase
import com.chillsam.courmy.course.domain.EndCourseTraceUseCase
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.domain.StartCourseTraceUseCase
import com.chillsam.courmy.course.entity.TraceVisitVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 따라가기 화면 ViewModel(FS-21).
 *
 * 코스 데이터는 상세와 같은 BFF 응답([GetCourseDetailUseCase])을 그대로 쓴다 — 따라가기 전용
 * 조회 API 가 없고, 필요한 것(장소 순서·좌표·도보 시간)이 상세 응답에 다 있다.
 *
 * 진행 상태(현재 장소·방문 수)는 화면 세션 상태고, 서버 trace(시작·방문 체크·종료)에는 통계용으로
 * **기록만** 한다 — 기록 실패가 따라가기를 막지 않도록 전부 조용히 삼키고 로그만 남긴다.
 * 시작 기록이 실패하면 traceId 가 없으므로 이후 체크인·종료 기록도 건너뛴다.
 */
@HiltViewModel
class CourseFollowViewModel
    @Inject
    constructor(
        private val getCourseDetailUseCase: GetCourseDetailUseCase,
        private val startCourseTrace: StartCourseTraceUseCase,
        private val checkInTracePlace: CheckInTracePlaceUseCase,
        private val endCourseTrace: EndCourseTraceUseCase,
    ) : MviViewModel<CourseFollowIntent, CourseFollowUIState, CourseFollowReducerEvent>(
            CourseFollowUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var courseId: Long? = null

        /** 서버가 발급한 따라가기 기록 id. 시작 기록이 실패하면 null 로 남아 이후 기록을 건너뛴다. */
        private var traceId: Long? = null

        /** 이번 따라가기의 방문 기록. 종료 시 전체를 함께 보낸다(체크인 유실 보완). */
        private val visits = mutableListOf<TraceVisitVO>()

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
                    recordStart()
                }

                CourseFollowIntent.CheckArrival -> {
                    val state = uiState.value
                    val total = state.detail?.places?.size ?: return
                    // 마지막 장소의 방문 체크면 완주 — 종료 시각을 함께 기록한다.
                    val isLast = state.currentIndex >= total - 1
                    val finishedAtMillis = if (isLast) System.currentTimeMillis() else null
                    recordVisit(state, finishedAtMillis)
                    dispatch(CourseFollowReducerEvent.Visited(finishedAtMillis = finishedAtMillis))
                }

                CourseFollowIntent.ConsumeError -> {
                    dispatch(CourseFollowReducerEvent.ErrorConsumed)
                }
            }
        }

        /** 따라가기 시작을 서버에 기록한다. 실패해도 화면 진행은 그대로 — 통계만 빠진다. */
        private fun recordStart() {
            val id = courseId ?: return
            traceId = null
            visits.clear()
            viewModelScope.launch {
                runCatching { startCourseTrace(id) }
                    .onSuccess { traceId = it }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "따라가기 시작 기록 실패(진행은 계속): courseId=$id", e)
                    }
            }
        }

        /** 방문 체크를 기록하고, 완주면 종료 기록까지 보낸다. */
        private fun recordVisit(
            state: CourseFollowUIState,
            finishedAtMillis: Long?,
        ) {
            val place = state.currentPlace ?: return
            val visit = TraceVisitVO(placeId = place.placeId, visitedAt = Instant.now().toString())
            visits += visit
            val trace = traceId ?: return
            // 종료까지 한 코루틴에서 순서대로 보낸다 — 마지막 체크인이 종료보다 늦게 도착하지 않게.
            val endingVisits = if (finishedAtMillis != null) visits.toList() else null
            val startedAt = state.startedAtMillis
            viewModelScope.launch {
                runCatching { checkInTracePlace(trace, visit) }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "방문 체크 기록 실패(진행은 계속): placeId=${visit.placeId}", e)
                    }
                if (endingVisits != null && finishedAtMillis != null) {
                    val durationMinutes =
                        if (startedAt != null && finishedAtMillis > startedAt) {
                            TimeUnit.MILLISECONDS.toMinutes(finishedAtMillis - startedAt).toInt()
                        } else {
                            0
                        }
                    runCatching { endCourseTrace(trace, endingVisits, durationMinutes) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "따라가기 종료 기록 실패: traceId=$trace", e)
                        }
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
