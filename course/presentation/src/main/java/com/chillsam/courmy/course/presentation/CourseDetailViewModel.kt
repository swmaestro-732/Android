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
 * 코스 상세 화면 ViewModel(FS-11). [CourseDetailIntent.Load] 로 받은 코스를 BFF 상세 API 로 조회한다.
 *
 * 생성 시점에는 조회하지 않는다. 라우트 인자가 화면에서 넘어와야 코스가 정해지는데,
 * init 에서 미리 부르면 아직 값이 없는 id(0)로 요청이 한 번 나가 404 가 뜬다.
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

        /** 조회 대상 코스. null 이면 아직 [CourseDetailIntent.Load] 를 받지 못한 상태다. */
        private var courseId: Long? = null

        override fun onIntent(intent: CourseDetailIntent) {
            when (intent) {
                is CourseDetailIntent.Load -> {
                    // 같은 코스로 재구성되면 다시 부르지 않는다(화면 회전·재진입).
                    if (courseId == intent.courseId) return
                    courseId = intent.courseId
                    load()
                }

                CourseDetailIntent.Retry -> {
                    load()
                }
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
            // 라우트 인자가 없거나 숫자가 아니면 0 이 넘어온다. 서버에 물어볼 것도 없이 에러로 끝낸다.
            val id = courseId
            if (id == null || id <= 0L) {
                Log.w(TAG, "잘못된 코스 id: $id")
                dispatch(CourseDetailReducerEvent.Failed("코스를 찾을 수 없습니다."))
                return
            }
            dispatch(CourseDetailReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    Log.d(TAG, "코스 상세 로드 시작: courseId=$id")
                    runCatching { getCourseDetailUseCase(id) }
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

        private companion object {
            const val TAG = "CourseDetail"
        }
    }
