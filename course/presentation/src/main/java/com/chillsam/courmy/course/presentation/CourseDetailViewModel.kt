package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
            viewModelScope.launch {
                Log.d(TAG, "코스 상세 로드 시작: courseId=$DEFAULT_COURSE_ID")
                runCatching { getCourseDetailUseCase(DEFAULT_COURSE_ID) }
                    .onSuccess { detail ->
                        Log.d(
                            TAG,
                            "로드 성공: title='${detail.title}', 장소 ${detail.places.size}곳, 리뷰 ${detail.reviews.size}건, " +
                                "커버=${detail.coverImageUrl.ifBlank { "(없음)" }}",
                        )
                        dispatch(CourseDetailReducerEvent.Loaded(detail))
                    }.onFailure { e ->
                        // 실패 원인(타임아웃/404/파싱 등)을 스택트레이스까지 남긴다.
                        Log.w(TAG, "로드 실패: ${e.javaClass.simpleName} - ${e.message}", e)
                        dispatch(CourseDetailReducerEvent.Failed(e.message ?: "코스를 불러오지 못했습니다."))
                    }
            }
        }

        private companion object {
            const val TAG = "CourseDetail"

            /** 목 백엔드가 제공하는 유일한 코스 id. */
            const val DEFAULT_COURSE_ID = 1L
        }
    }
