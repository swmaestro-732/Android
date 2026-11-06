package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CheckInTracePlaceUseCase
import com.chillsam.courmy.course.domain.EndCourseTraceUseCase
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.domain.GetTraceRouteUseCase
import com.chillsam.courmy.course.domain.StartCourseTraceUseCase
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.TraceVisitVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
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
        private val getTraceRoute: GetTraceRouteUseCase,
    ) : MviViewModel<CourseFollowIntent, CourseFollowUIState, CourseFollowReducerEvent>(
            CourseFollowUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var startJob: Job? = null
        private var routeJob: Job? = null
        private var courseId: Long? = null

        /** 서버가 발급한 따라가기 기록 id. 시작 기록이 실패하면 null 로 남아 이후 기록을 건너뛴다. */
        private var traceId: Long? = null

        /** 이번 따라가기의 방문 기록. 종료 시 전체를 함께 보낸다(체크인 유실 보완). */
        private val visits = mutableListOf<TraceVisitVO>()

        /** 이어가기(홈 진행 중인 코스 카드)로 들어왔을 때의 진행 정보. 일반 진입이면 null. */
        private var pendingResume: ResumeInfo? = null

        override fun onIntent(intent: CourseFollowIntent) {
            when (intent) {
                is CourseFollowIntent.Load -> {
                    // 같은 코스로 재진입하면 다시 받지 않는다 — 진행 중 상태(방문 기록)를 지키기 위해서다.
                    if (courseId == intent.courseId && uiState.value.detail != null) return
                    courseId = intent.courseId
                    pendingResume =
                        if (intent.resumeTraceId > 0 && intent.resumeVisitedCount > 0) {
                            ResumeInfo(intent.resumeTraceId, intent.resumeVisitedCount, intent.resumeStartedAt)
                        } else {
                            null
                        }
                    load()
                }

                CourseFollowIntent.Retry -> {
                    load()
                }

                CourseFollowIntent.Start -> {
                    start()
                }

                CourseFollowIntent.CheckArrival -> {
                    checkArrival()
                }

                CourseFollowIntent.ConsumeError -> {
                    dispatch(CourseFollowReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun start() {
            if (currentState.phase != CourseFollowPhase.PREVIEW || currentState.detail?.places.isNullOrEmpty()) return
            // 이어가기: 진행 중 세션이 있으면 처음부터가 아니라 현재 진행도에서 TRACKING 으로 연다.
            val resume = pendingResume
            if (resume != null) {
                pendingResume = null
                traceId = resume.traceId
                val startedAtMillis =
                    resume.startedAt
                        .takeIf { it.isNotBlank() }
                        ?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }
                        ?: System.currentTimeMillis()
                dispatch(
                    CourseFollowReducerEvent.Resumed(
                        currentIndex = resume.visitedCount,
                        visitedCount = resume.visitedCount,
                        startedAtMillis = startedAtMillis,
                    ),
                )
                return
            }
            dispatch(CourseFollowReducerEvent.Started(System.currentTimeMillis()))
            recordStart()
        }

        private fun checkArrival() {
            val state = currentState
            if (state.phase != CourseFollowPhase.TRACKING || state.isRecording) return
            val total = state.detail?.places?.size ?: return
            val finishedAtMillis = if (state.currentIndex >= total - 1) System.currentTimeMillis() else null
            recordVisit(state, finishedAtMillis)
        }

        /** 따라가기 시작을 서버에 기록한다. 실패해도 화면 진행은 그대로 — 통계만 빠진다. */
        private fun recordStart() {
            val id = courseId ?: return
            traceId = null
            visits.clear()
            startJob =
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
            // 종료까지 한 코루틴에서 순서대로 보낸다 — 마지막 체크인이 종료보다 늦게 도착하지 않게.
            val endingVisits = if (finishedAtMillis != null) visits.toList() else null
            val startedAt = state.startedAtMillis
            dispatch(CourseFollowReducerEvent.RecordingStarted)
            viewModelScope.launch {
                startJob?.join()
                val trace = traceId
                if (trace == null) {
                    dispatch(CourseFollowReducerEvent.Visited(finishedAtMillis))
                    return@launch
                }
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
                dispatch(CourseFollowReducerEvent.Visited(finishedAtMillis))
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
                            offerResume(detail)
                            loadRoutes(detail)
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "따라가기 코스 로드 실패: courseId=$id", e)
                            dispatch(CourseFollowReducerEvent.LoadFailed("코스를 불러오지 못했어요."))
                        }
                }
        }

        /**
         * 이어가기로 들어왔으면 코스를 받은 직후 **미리보기는 그대로 두고** 버튼만 "이어가기"로 바꾼다.
         * 실제 진행(TRACKING) 전환은 사용자가 그 버튼을 눌러 [start] 에서 일어난다.
         *
         * 코스는 순서대로 따라간다는 가정 아래 방문 수가 1 이상·전체 미만일 때만 이어갈 수 있다
         * (0 이면 처음부터, 전체 이상이면 모두 방문·미종료라 이어갈 장소가 없어 일반 미리보기).
         */
        private fun offerResume(detail: CourseDetailVO) {
            val resume = pendingResume ?: return
            val total = detail.places.size
            if (resume.visitedCount !in 1 until total) {
                pendingResume = null
                return
            }
            dispatch(CourseFollowReducerEvent.ResumeOffered(resume.visitedCount))
        }

        /**
         * 코스를 받으면 좌표가 있는 연속 장소쌍마다 도보 경로를 **병렬로** 조회해 지도에 실제 경로를 채운다.
         *
         * 지도는 부가 정보라 한 구간이 실패해도 다른 구간·진행을 막지 않는다 — 실패는 구간별로 [RouteFailed]
         * 로만 남기고(지도는 직선 폴백, 화면은 외부 지도 안내), 예외를 위로 던지지 않는다.
         */
        private fun loadRoutes(detail: CourseDetailVO) {
            val segments =
                detail.places
                    .filter { it.latitude != null && it.longitude != null && it.placeId > 0 }
                    .zipWithNext()
                    .filter { (from, to) -> from.placeId != to.placeId }
            if (segments.isEmpty()) return
            routeJob?.cancel()
            dispatch(CourseFollowReducerEvent.RoutesRequested(segments.map { it.first.placeId }))
            routeJob =
                viewModelScope.launch {
                    segments.forEach { (from, to) ->
                        launch {
                            runCatching { getTraceRoute(from.placeId, to.placeId) }
                                .onSuccess { dispatch(CourseFollowReducerEvent.RouteLoaded(from.placeId, it)) }
                                .onFailure { e ->
                                    if (e is CancellationException) throw e
                                    Log.w(TAG, "도보 경로 조회 실패(직선 폴백): ${from.placeId}→${to.placeId}", e)
                                    dispatch(CourseFollowReducerEvent.RouteFailed(from.placeId))
                                }
                        }
                    }
                }
        }

        override fun reduce(
            state: CourseFollowUIState,
            event: CourseFollowReducerEvent,
        ): CourseFollowUIState =
            when (event) {
                CourseFollowReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null, routes = persistentMapOf())
                }

                is CourseFollowReducerEvent.RoutesRequested -> {
                    state.copy(
                        routes = event.fromPlaceIds.associateWith { FollowRouteStatus.Loading }.toPersistentMap(),
                    )
                }

                is CourseFollowReducerEvent.RouteLoaded -> {
                    state.copy(routes = state.routes.put(event.fromPlaceId, FollowRouteStatus.Ready(event.route)))
                }

                is CourseFollowReducerEvent.RouteFailed -> {
                    state.copy(routes = state.routes.put(event.fromPlaceId, FollowRouteStatus.Failed))
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

                is CourseFollowReducerEvent.ResumeOffered -> {
                    state.copy(resumeVisitedCount = event.visitedCount)
                }

                is CourseFollowReducerEvent.Resumed -> {
                    state.copy(
                        phase = CourseFollowPhase.TRACKING,
                        startedAtMillis = event.startedAtMillis,
                        currentIndex = event.currentIndex,
                        visitedCount = event.visitedCount,
                    )
                }

                is CourseFollowReducerEvent.Visited -> {
                    if (event.finishedAtMillis != null) {
                        state.copy(
                            isRecording = false,
                            phase = CourseFollowPhase.DONE,
                            visitedCount = state.visitedCount + 1,
                            finishedAtMillis = event.finishedAtMillis,
                        )
                    } else {
                        state.copy(
                            isRecording = false,
                            visitedCount = state.visitedCount + 1,
                            currentIndex = state.currentIndex + 1,
                        )
                    }
                }

                CourseFollowReducerEvent.RecordingStarted -> {
                    state.copy(isRecording = true)
                }

                CourseFollowReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private companion object {
            const val TAG = "CourseFollow"
        }
    }

/** 이어가기 진입 정보. 진행 중 세션 id·방문 수·시작 시각(ISO-8601 UTC). */
private data class ResumeInfo(
    val traceId: Long,
    val visitedCount: Int,
    val startedAt: String,
)
