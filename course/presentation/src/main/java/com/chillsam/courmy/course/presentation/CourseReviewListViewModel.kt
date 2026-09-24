package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.auth.IsLoggedInUseCase
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.DeleteCourseReviewUseCase
import com.chillsam.courmy.course.domain.GetCourseReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 후기 전체보기 ViewModel(FS-37).
 *
 * 정렬·페이징은 **서버가 한다**. 받아 온 목록을 앱에서 다시 늘어놓으면 커서 페이징과 어긋나서
 * (2페이지를 붙일 때 앞 페이지까지 섞여) 순서가 뒤집히기 때문이다. 그래서 정렬 탭을 바꾸면
 * 커서를 버리고 처음부터 다시 받는다.
 */
@HiltViewModel
class CourseReviewListViewModel
    @Inject
    constructor(
        private val getCourseReviewsUseCase: GetCourseReviewsUseCase,
        private val deleteCourseReviewUseCase: DeleteCourseReviewUseCase,
        private val isLoggedInUseCase: IsLoggedInUseCase,
    ) : MviViewModel<CourseReviewListIntent, CourseReviewListUIState, CourseReviewListReducerEvent>(
            CourseReviewListUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var loadMoreJob: Job? = null
        private var deleteJob: Job? = null
        private var loadedCourseId: Long? = null

        override fun onIntent(intent: CourseReviewListIntent) {
            when (intent) {
                is CourseReviewListIntent.Load -> {
                    // 화면 재구성마다 서버를 치지 않도록 같은 코스면 건너뛴다.
                    if (loadedCourseId == intent.courseId) return
                    loadedCourseId = intent.courseId
                    load()
                }

                CourseReviewListIntent.Retry -> {
                    load()
                }

                CourseReviewListIntent.ReloadAfterWrite -> {
                    load()
                }

                is CourseReviewListIntent.ChangeSort -> {
                    if (intent.sort == currentState.sort) return
                    dispatch(CourseReviewListReducerEvent.SortChanged(intent.sort))
                    load()
                }

                CourseReviewListIntent.LoadMore -> {
                    loadMore()
                }

                is CourseReviewListIntent.DeleteReview -> {
                    delete(intent.reviewId)
                }

                CourseReviewListIntent.ConsumeMessage -> {
                    dispatch(CourseReviewListReducerEvent.MessageConsumed)
                }
            }
        }

        private fun load() {
            val courseId = loadedCourseId ?: return
            // 첫 페이지를 다시 받는 동안 진행 중이던 추가 로드가 끝나면 엉뚱한 페이지가 붙는다.
            loadMoreJob?.cancel()
            loadJob?.cancel()
            dispatch(CourseReviewListReducerEvent.LoadStarted)
            loadJob =
                viewModelScope.launch {
                    runCatching { getCourseReviewsUseCase(courseId = courseId, sort = currentState.sort) }
                        .onSuccess {
                            dispatch(
                                CourseReviewListReducerEvent.Loaded(
                                    list = it,
                                    isLoggedIn = isLoggedInUseCase(),
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "후기 목록 조회 실패: courseId=$courseId", e)
                            dispatch(CourseReviewListReducerEvent.LoadFailed("후기를 불러오지 못했어요."))
                        }
                }
        }

        /** 목록 끝에 닿았을 때 다음 페이지를 잇는다. 커서가 없거나 이미 받는 중이면 아무것도 하지 않는다. */
        private fun loadMore() {
            val courseId = loadedCourseId ?: return
            val state = currentState
            // 첫 페이지를 받는 중에도 스크롤 감지가 발화할 수 있다. 둘이 겹치면 같은 페이지가 두 번 붙는다.
            val isBusy = state.isLoadingMore || state.isLoading
            // 커서가 없으면 다음 요청을 만들 수 없다. hasNext 만 true 인 응답도 여기서 걸린다.
            val cursor = state.nextCursor?.takeIf { state.hasNext && !isBusy } ?: return
            dispatch(CourseReviewListReducerEvent.LoadMoreStarted)
            loadMoreJob?.cancel()
            loadMoreJob =
                viewModelScope.launch {
                    runCatching {
                        getCourseReviewsUseCase(courseId = courseId, sort = state.sort, cursor = cursor)
                    }.onSuccess { dispatch(CourseReviewListReducerEvent.MoreLoaded(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "후기 다음 페이지 조회 실패: courseId=$courseId, cursor=$cursor", e)
                            dispatch(CourseReviewListReducerEvent.LoadMoreFailed("후기를 더 불러오지 못했어요."))
                        }
                }
        }

        /**
         * 내 후기 삭제. 되돌릴 수 없어 중복 탭을 막고, 서버가 확정한 뒤에만 목록에서 뺀다.
         *
         * 서버는 남의 리뷰·이미 지운 리뷰를 구분하지 않고 404 로 답하므로 사유를 나눠 안내할 수 없다.
         */
        private fun delete(reviewId: Long) {
            val courseId = loadedCourseId ?: return
            if (currentState.deletingReviewId != 0L || reviewId <= 0L) return
            dispatch(CourseReviewListReducerEvent.DeleteStarted(reviewId))
            deleteJob?.cancel()
            deleteJob =
                viewModelScope.launch {
                    runCatching { deleteCourseReviewUseCase(courseId = courseId, reviewId = reviewId) }
                        .onSuccess {
                            dispatch(CourseReviewListReducerEvent.Deleted(reviewId))
                            // 목록에서 빼는 것만으로는 평균·분포·총 개수가 그대로라 집계가 어긋난다.
                            // 작성 버튼도 다시 열려야 하므로 첫 페이지부터 새로 받는다.
                            load()
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "후기 삭제 실패: courseId=$courseId, reviewId=$reviewId", e)
                            val message =
                                if ((e as? HttpResponseException)?.rawCode == NOT_FOUND) {
                                    "이미 삭제된 후기예요."
                                } else {
                                    "후기를 삭제하지 못했어요."
                                }
                            dispatch(CourseReviewListReducerEvent.DeleteFailed(message))
                        }
                }
        }

        override fun reduce(
            state: CourseReviewListUIState,
            event: CourseReviewListReducerEvent,
        ): CourseReviewListUIState =
            when (event) {
                CourseReviewListReducerEvent.LoadStarted -> {
                    state.copy(
                        isLoading = true,
                        errorMessage = null,
                        isLoadingMore = false,
                        // 정렬이 바뀌었을 수 있어 이전 페이지 커서를 버린다.
                        nextCursor = null,
                        hasNext = false,
                    )
                }

                is CourseReviewListReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        errorMessage = null,
                        summary = event.list,
                        reviews = event.list.reviews.toImmutableList(),
                        nextCursor = event.list.nextCursor,
                        hasNext = event.list.hasNext,
                        // 화면을 보는 동안 세션이 끊길 수 있어 조회할 때마다 다시 본다.
                        isLoggedIn = event.isLoggedIn,
                    )
                }

                is CourseReviewListReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is CourseReviewListReducerEvent.SortChanged -> {
                    state.copy(sort = event.sort)
                }

                CourseReviewListReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is CourseReviewListReducerEvent.MoreLoaded -> {
                    state.copy(
                        isLoadingMore = false,
                        // 집계(평균·분포·사진 띠)는 첫 페이지 것을 유지한다.
                        reviews = (state.reviews + event.page.reviews).toImmutableList(),
                        nextCursor = event.page.nextCursor,
                        hasNext = event.page.hasNext,
                    )
                }

                is CourseReviewListReducerEvent.LoadMoreFailed -> {
                    // 커서는 남겨 둔다. 다시 스크롤하면 같은 페이지를 재시도한다.
                    state.copy(isLoadingMore = false, message = event.message)
                }

                is CourseReviewListReducerEvent.DeleteStarted -> {
                    state.copy(deletingReviewId = event.reviewId)
                }

                is CourseReviewListReducerEvent.Deleted -> {
                    state.copy(
                        deletingReviewId = 0L,
                        reviews = state.reviews.filterNot { it.id == event.reviewId }.toImmutableList(),
                        message = "후기를 삭제했어요.",
                    )
                }

                is CourseReviewListReducerEvent.DeleteFailed -> {
                    state.copy(deletingReviewId = 0L, message = event.message)
                }

                CourseReviewListReducerEvent.MessageConsumed -> {
                    state.copy(message = null)
                }
            }

        private companion object {
            const val TAG = "CourseReviewList"
            const val NOT_FOUND = 404
        }
    }
