package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.entity.CourseDetailVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코스 상세 화면 ViewModel(FS-11). 진입 시 BFF 코스 상세 API 를 호출해 상태로 노출한다.
 *
 * 라우트가 아직 courseId 를 싣지 않아(제목/생성시각만 전달) 목 데이터가 제공하는 [DEFAULT_COURSE_ID] 를 조회한다.
 * 라우트에 courseId 인자가 추가되면 SavedStateHandle 로 받아 교체한다.
 */
@HiltViewModel
class CourseDetailViewModel
    @Inject
    constructor(
        private val getCourseDetailUseCase: GetCourseDetailUseCase,
    ) : MviViewModel<CourseDetailIntent, CourseDetailUIState, CourseDetailReducerEvent>(
            CourseDetailUIState.empty,
        ) {
        /** 진행 중인 로드 코루틴. 재요청 시 이전 것을 취소해 중복 실행·stale 결과 반영을 막는다. */
        private var loadJob: Job? = null

        init {
            onIntent(CourseDetailIntent.Load)
        }

        override fun onIntent(intent: CourseDetailIntent) {
            when (intent) {
                CourseDetailIntent.Load,
                CourseDetailIntent.Retry,
                -> load()
            }
        }

        override fun reduce(
            state: CourseDetailUIState,
            event: CourseDetailReducerEvent,
        ): CourseDetailUIState =
            when (event) {
                CourseDetailReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is CourseDetailReducerEvent.Loaded -> {
                    state.copy(isLoading = false, detail = event.detail, errorMessage = null)
                }

                is CourseDetailReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }
            }

        private fun load() {
            dispatch(CourseDetailReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    Log.d(TAG, "코스 상세 로드 시작: courseId=$DEFAULT_COURSE_ID")
                    runCatching { getCourseDetailUseCase(DEFAULT_COURSE_ID).withSampleFallbackInDebug() }
                        .onSuccess { detail ->
                            Log.d(
                                TAG,
                                "로드 성공: title='${detail.title}', " +
                                    "장소 ${detail.places.size}곳, 리뷰 ${detail.reviews.size}건, " +
                                    "커버=${detail.coverImageUrl.ifBlank { "(없음)" }}",
                            )
                            dispatch(CourseDetailReducerEvent.Loaded(detail))
                        }.onFailure { e ->
                            // 협력적 취소(ViewModel clear·재요청 등)는 실패가 아니므로 그대로 전파한다.
                            if (e is CancellationException) throw e
                            // 실패 원인(타임아웃/404/파싱 등)을 스택트레이스까지 남긴다.
                            Log.w(TAG, "로드 실패: ${e.javaClass.simpleName} - ${e.message}", e)
                            dispatch(CourseDetailReducerEvent.Failed(e.message ?: "코스를 불러오지 못했습니다."))
                        }
                }
        }

        /**
         * 디버그 빌드 한정: API 응답에 장소 좌표가 없어 "코스 경로" 지도가 안 뜨면
         * 좌표 포함 더미([courseDetailSample])로 대체해 화면을 확인한다. 릴리스는 실데이터 그대로.
         * API 에 장소 좌표(CoursePlaceDTO + toVO)가 추가되면 제거한다. [wiki-needed]
         */
        private fun CourseDetailVO.withSampleFallbackInDebug(): CourseDetailVO {
            val hasNoMappablePlace = places.none { it.latitude != null && it.longitude != null }
            return if (BuildConfig.DEBUG && hasNoMappablePlace) courseDetailSample else this
        }

        private companion object {
            const val TAG = "CourseDetail"

            /** 목 백엔드가 제공하는 유일한 코스 id. */
            const val DEFAULT_COURSE_ID = 1L
        }
    }
