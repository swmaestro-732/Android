package com.chillsam.courmy.main.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.EndCourseTraceUseCase
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.main.domain.home.GetHomeUseCase
import com.chillsam.courmy.main.presentation.location.DefaultLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * 홈 화면(FS-09) ViewModel. `GET /service/v2/home` 을 [GetHomeUseCase] 로 로드한다.
 *
 * 공개 엔드포인트라 로그인 여부와 무관하게 호출한다(게스트 홈에서도 추천 코스가 보인다).
 * 근처 저장 장소는 서버가 JWT 로 사용자를 식별해 주므로 게스트에서는 비어 온다.
 *
 * 생성 시점에는 로드하지 않는다. 화면이 RefreshOnResume 으로 부르므로,
 * init 에서도 부르면 진입할 때 같은 요청이 두 번 나간다.
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getHomeUseCase: GetHomeUseCase,
        private val endCourseTraceUseCase: EndCourseTraceUseCase,
        private val setCourseSavedUseCase: SetCourseSavedUseCase,
    ) : MviViewModel<HomeIntent, HomeUIState, HomeReducerEvent>(
            HomeUIState.empty,
        ) {
        private var loadJob: Job? = null

        /** 기기 현위치. 확보 전에는 null 이라 기본 기준점으로 조회한다. */
        private var myLat: Double? = null
        private var myLng: Double? = null

        override fun onIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.Retry -> {
                    load()
                }

                is HomeIntent.UpdateLocation -> {
                    // 기준점이 실제로 바뀌었을 때만 다시 불러 재진입마다 중복 조회를 막는다.
                    val moved =
                        myLat == null || myLng == null ||
                            kotlin.math.abs(myLat!! - intent.lat) > LOCATION_EPSILON ||
                            kotlin.math.abs(myLng!! - intent.lng) > LOCATION_EPSILON
                    myLat = intent.lat
                    myLng = intent.lng
                    if (moved) load()
                }

                HomeIntent.DismissInProgressCourse -> {
                    endInProgressTrace()
                }

                HomeIntent.ConsumeInProgressEnded -> {
                    dispatch(HomeReducerEvent.InProgressEndedConsumed)
                }
            }
        }

        /**
         * 진행 중인 코스 카드의 X(종료). 따라가기 세션을 서버에서 종료(`POST /api/v1/traces/{traceId}/end`)하고
         * 홈 카드를 숨긴다. 서버는 중간 종료도 완주(COMPLETED)로 기록한다 — 별도 포기 API 는 없다.
         *
         * 방문 장소는 체크인 때 이미 올라갔으므로 종료 본문엔 빈 목록을 보낸다(서버가 이미 있는 장소는 무시).
         * 소요 시간은 startedAt~지금으로 계산하고, 이동 거리는 홈에 값이 없어 0 을 보낸다(서버 최소값 0 허용).
         *
         * 카드는 낙관적으로 먼저 숨긴다 — 종료 기록은 네트워크를 기다리지 않으며, 실패하면 다음 새로고침에
         * 서버가 아직 진행 중인 세션을 다시 내려줘 스스로 보정된다(이미 종료됐으면 409 는 무시).
         */
        private fun endInProgressTrace() {
            val course = currentState.inProgressCourse ?: return
            dispatch(HomeReducerEvent.InProgressDismissed)
            val traceId = course.traceId
            if (traceId <= 0L) return
            viewModelScope.launch {
                runCatching {
                    endCourseTraceUseCase(
                        traceId = traceId,
                        visits = emptyList(),
                        durationMinutes = elapsedMinutesSince(course.startedAt),
                        distanceMeters = 0,
                    )
                }.onSuccess {
                    // 저장함 목록은 저장(북마크)한 코스만 보이므로, 완주 기록이 목록에 나타나게 저장도 함께 한다.
                    // 이미 저장돼 있으면 실패해도 목록에 있으니 삼키고 넘어간다(이동은 계속).
                    runCatching { setCourseSavedUseCase(courseId = course.courseId, saved = true) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "종료 코스 저장 실패(무시): courseId=${course.courseId}", e)
                        }
                    // 서버가 완주(COMPLETED)로 기록했다 — 화면이 저장함(완주)으로 보내게 알린다.
                    dispatch(HomeReducerEvent.InProgressEnded)
                }.onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.w(TAG, "진행 중인 코스 종료 실패: traceId=$traceId", e)
                }
            }
        }

        /** startedAt(ISO-8601 UTC)부터 지금까지 경과(분). 파싱 실패·미래 시각이면 0. */
        private fun elapsedMinutesSince(startedAt: String): Int {
            val started = runCatching { Instant.parse(startedAt) }.getOrNull() ?: return 0
            return ChronoUnit.MINUTES
                .between(started, Instant.now())
                .coerceAtLeast(0L)
                .toInt()
        }

        override fun reduce(
            state: HomeUIState,
            event: HomeReducerEvent,
        ): HomeUIState =
            when (event) {
                HomeReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is HomeReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        courses = event.courses.toImmutableList(),
                        nearbyPlaces = event.nearbyPlaces.toImmutableList(),
                        inProgressCourse = event.inProgressCourse,
                        errorMessage = null,
                    )
                }

                is HomeReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                HomeReducerEvent.InProgressDismissed -> {
                    state.copy(inProgressCourse = null)
                }

                HomeReducerEvent.InProgressEnded -> {
                    state.copy(inProgressEnded = true)
                }

                HomeReducerEvent.InProgressEndedConsumed -> {
                    state.copy(inProgressEnded = false)
                }
            }

        private fun load() {
            dispatch(HomeReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching {
                        getHomeUseCase(
                            // 거리 기준점은 기기 현위치. 권한 전에는 기본 진입 지점 폴백(SCRUM-634).
                            userLat = myLat ?: DefaultLocation.LATITUDE,
                            userLng = myLng ?: DefaultLocation.LONGITUDE,
                        )
                    }.onSuccess { home ->
                        dispatch(
                            HomeReducerEvent.Loaded(
                                courses = home.recommendedCourses,
                                nearbyPlaces = home.nearbySavedPlaces,
                                inProgressCourse = home.inProgressCourse,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                        Log.w(TAG, "홈 로드 실패", e)
                        dispatch(HomeReducerEvent.Failed("코스를 불러오지 못했습니다."))
                    }
                }
        }

        private companion object {
            const val TAG = "Home"

            /** 위경도 약 0.0005도 = 수십 m. 이보다 작은 이동은 재조회하지 않는다. */
            const val LOCATION_EPSILON = 0.0005
        }
    }
