package com.chillsam.courmy.main.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.home.GetHomeUseCase
import com.chillsam.courmy.main.presentation.location.DefaultLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    ) : MviViewModel<HomeIntent, HomeUIState, HomeReducerEvent>(
            HomeUIState.empty,
        ) {
        private var loadJob: Job? = null

        override fun onIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.Retry -> load()
            }
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
                        errorMessage = null,
                    )
                }

                is HomeReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }
            }

        private fun load() {
            dispatch(HomeReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching {
                        getHomeUseCase(
                            // 위치 권한 연동 전이라 거리 기준점은 지도 기본 진입 지점과 같은 값을 쓴다.
                            userLat = DefaultLocation.LATITUDE,
                            userLng = DefaultLocation.LONGITUDE,
                        )
                    }.onSuccess { home ->
                        dispatch(
                            HomeReducerEvent.Loaded(
                                courses = home.recommendedCourses,
                                nearbyPlaces = home.nearbySavedPlaces,
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
        }
    }
