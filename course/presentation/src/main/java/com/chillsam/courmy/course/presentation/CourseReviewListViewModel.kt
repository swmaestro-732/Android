package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.GetCourseReviewsUseCase
import com.chillsam.courmy.course.entity.CourseReviewVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 후기 전체보기 ViewModel(FS-37).
 *
 * 정렬은 서버가 지원하지 않아 받아 온 목록을 여기서 다시 늘어놓는다. 정렬만 바꿀 때는 서버를 다시
 * 치지 않는다 — 같은 데이터를 다시 받아 봐야 순서만 바뀌기 때문이다.
 */
@HiltViewModel
class CourseReviewListViewModel
    @Inject
    constructor(
        private val getCourseReviewsUseCase: GetCourseReviewsUseCase,
    ) : MviViewModel<CourseReviewListIntent, CourseReviewListUIState, CourseReviewListReducerEvent>(
            CourseReviewListUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var loadedCourseId: Long? = null

        override fun onIntent(intent: CourseReviewListIntent) {
            when (intent) {
                is CourseReviewListIntent.Load -> {
                    // 화면 재구성마다 서버를 치지 않도록 같은 코스면 건너뛴다.
                    if (loadedCourseId == intent.courseId) return
                    load(intent.courseId)
                }

                CourseReviewListIntent.Retry -> {
                    loadedCourseId?.let(::load)
                }

                is CourseReviewListIntent.ChangeSort -> {
                    dispatch(CourseReviewListReducerEvent.SortChanged(intent.sort))
                }
            }
        }

        private fun load(courseId: Long) {
            loadedCourseId = courseId
            loadJob?.cancel()
            dispatch(CourseReviewListReducerEvent.LoadStarted)
            loadJob =
                viewModelScope.launch {
                    runCatching { getCourseReviewsUseCase(courseId) }
                        .onSuccess { dispatch(CourseReviewListReducerEvent.Loaded(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "후기 목록 조회 실패: courseId=$courseId", e)
                            dispatch(CourseReviewListReducerEvent.LoadFailed("후기를 불러오지 못했어요."))
                        }
                }
        }

        override fun reduce(
            state: CourseReviewListUIState,
            event: CourseReviewListReducerEvent,
        ): CourseReviewListUIState =
            when (event) {
                CourseReviewListReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is CourseReviewListReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        errorMessage = null,
                        summary = event.list,
                        reviews = event.list.reviews.sortedBy(state.sort),
                    )
                }

                is CourseReviewListReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is CourseReviewListReducerEvent.SortChanged -> {
                    state.copy(
                        sort = event.sort,
                        // 아직 안 불러왔으면 정렬할 목록이 없다. 탭 상태만 바꿔 둔다.
                        reviews =
                            state.summary
                                ?.reviews
                                .orEmpty()
                                .sortedBy(event.sort),
                    )
                }
            }

        private companion object {
            const val TAG = "CourseReviewList"
        }
    }

/**
 * 정렬 적용. [CourseReviewSort.LATEST] 는 서버가 준 순서를 그대로 쓰고,
 * [CourseReviewSort.RATING] 은 별점 내림차순으로 다시 늘어놓는다.
 *
 * `sortedByDescending` 은 안정 정렬이라 같은 별점끼리는 서버 순서(=최신순)가 유지된다.
 */
private fun List<CourseReviewVO>.sortedBy(sort: CourseReviewSort): ImmutableList<CourseReviewVO> =
    when (sort) {
        CourseReviewSort.LATEST -> this
        CourseReviewSort.RATING -> sortedByDescending { it.rating }
    }.toImmutableList()
