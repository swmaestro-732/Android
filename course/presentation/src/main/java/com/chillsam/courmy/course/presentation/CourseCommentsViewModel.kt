package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.domain.DeleteCourseCommentUseCase
import com.chillsam.courmy.course.domain.EditCourseCommentUseCase
import com.chillsam.courmy.course.domain.GetCourseCommentsUseCase
import com.chillsam.courmy.course.domain.WriteCourseCommentUseCase
import com.chillsam.courmy.course.entity.CourseCommentVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseCommentsUIState(
    val isLoading: Boolean = true,
    val comments: ImmutableList<CourseCommentVO> = persistentListOf(),
    val errorMessage: String? = null,
    /** 다음 페이지 커서(마지막 댓글 id). null 이면 끝. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val isLoadingMore: Boolean = false,
    /** 작성·수정·삭제 진행 중(입력바 잠금 + 중복 탭 방지). */
    val isSubmitting: Boolean = false,
    /** 입력바 내용. 작성 성공 시 비운다. */
    val draft: String = "",
    /** 일회성 안내(실패 등). 토스트로 보여주고 소비한다. */
    val message: String? = null,
) : UiState

sealed interface CourseCommentsIntent : MviIntent {
    data class Load(
        val courseId: Long,
    ) : CourseCommentsIntent

    data object Retry : CourseCommentsIntent

    data object LoadMore : CourseCommentsIntent

    data class ChangeDraft(
        val value: String,
    ) : CourseCommentsIntent

    data object Write : CourseCommentsIntent

    data class Edit(
        val commentId: Long,
        val content: String,
    ) : CourseCommentsIntent

    data class Delete(
        val commentId: Long,
    ) : CourseCommentsIntent

    data object ConsumeMessage : CourseCommentsIntent
}

sealed interface CourseCommentsReducerEvent : ReducerEvent {
    data object LoadStarted : CourseCommentsReducerEvent

    data class Loaded(
        val comments: List<CourseCommentVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : CourseCommentsReducerEvent

    data class LoadFailed(
        val message: String,
    ) : CourseCommentsReducerEvent

    data object LoadMoreStarted : CourseCommentsReducerEvent

    data class MoreLoaded(
        val comments: List<CourseCommentVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
    ) : CourseCommentsReducerEvent

    data class MoreFailed(
        val message: String,
    ) : CourseCommentsReducerEvent

    data class DraftChanged(
        val value: String,
    ) : CourseCommentsReducerEvent

    data object SubmitStarted : CourseCommentsReducerEvent

    /** 작성·수정·삭제 성공 뒤 첫 페이지를 다시 받아 통째로 교체한다(정렬·상대시각 일관성). */
    data class Refreshed(
        val comments: List<CourseCommentVO>,
        val nextCursor: String?,
        val hasNext: Boolean,
        val clearDraft: Boolean,
    ) : CourseCommentsReducerEvent

    data class SubmitFailed(
        val message: String,
    ) : CourseCommentsReducerEvent

    data object MessageConsumed : CourseCommentsReducerEvent
}

/**
 * 코스 댓글 화면 ViewModel. 목록은 커서로 이어 받고, 쓰기(작성·수정·삭제) 성공 후에는
 * 첫 페이지를 다시 받아 교체한다 — 서버 정렬을 로컬에서 흉내 내지 않는다.
 */
@HiltViewModel
class CourseCommentsViewModel
    @Inject
    constructor(
        private val getComments: GetCourseCommentsUseCase,
        private val writeComment: WriteCourseCommentUseCase,
        private val editComment: EditCourseCommentUseCase,
        private val deleteComment: DeleteCourseCommentUseCase,
    ) : MviViewModel<CourseCommentsIntent, CourseCommentsUIState, CourseCommentsReducerEvent>(
            CourseCommentsUIState(),
        ) {
        private var courseId = 0L
        private var loadJob: Job? = null
        private var moreJob: Job? = null
        private var submitJob: Job? = null

        override fun onIntent(intent: CourseCommentsIntent) {
            when (intent) {
                is CourseCommentsIntent.Load -> {
                    if (courseId == intent.courseId &&
                        (currentState.comments.isNotEmpty() || currentState.isLoading)
                    ) {
                        return
                    }
                    courseId = intent.courseId
                    load()
                }

                CourseCommentsIntent.Retry -> {
                    load()
                }

                CourseCommentsIntent.LoadMore -> {
                    loadMore()
                }

                is CourseCommentsIntent.ChangeDraft -> {
                    dispatch(CourseCommentsReducerEvent.DraftChanged(intent.value))
                }

                CourseCommentsIntent.Write -> {
                    submit(clearDraft = true) { writeComment(courseId, currentState.draft) }
                }

                is CourseCommentsIntent.Edit -> {
                    submit(clearDraft = false) { editComment(courseId, intent.commentId, intent.content) }
                }

                is CourseCommentsIntent.Delete -> {
                    submit(clearDraft = false) { deleteComment(courseId, intent.commentId) }
                }

                CourseCommentsIntent.ConsumeMessage -> {
                    dispatch(CourseCommentsReducerEvent.MessageConsumed)
                }
            }
        }

        private fun load() {
            loadJob?.cancel()
            moreJob?.cancel()
            dispatch(CourseCommentsReducerEvent.LoadStarted)
            loadJob =
                viewModelScope.launch {
                    runCatching { getComments(courseId) }
                        .onSuccess {
                            dispatch(CourseCommentsReducerEvent.Loaded(it.items, it.nextCursor, it.hasNext))
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "댓글 목록 로드 실패: courseId=$courseId", e)
                            dispatch(CourseCommentsReducerEvent.LoadFailed("댓글을 불러오지 못했어요."))
                        }
                }
        }

        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore) return
            val cursor = state.nextCursor?.takeIf { state.hasNext } ?: return
            dispatch(CourseCommentsReducerEvent.LoadMoreStarted)
            moreJob?.cancel()
            moreJob =
                viewModelScope.launch {
                    runCatching { getComments(courseId, cursor) }
                        .onSuccess {
                            dispatch(CourseCommentsReducerEvent.MoreLoaded(it.items, it.nextCursor, it.hasNext))
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "댓글 다음 페이지 로드 실패", e)
                            dispatch(CourseCommentsReducerEvent.MoreFailed("더 불러오지 못했어요."))
                        }
                }
        }

        /** 쓰기 공통 흐름: 요청 → 성공 시 첫 페이지 재로드로 교체. */
        private fun submit(
            clearDraft: Boolean,
            action: suspend () -> Unit,
        ) {
            if (submitJob?.isActive == true) return
            dispatch(CourseCommentsReducerEvent.SubmitStarted)
            submitJob =
                viewModelScope.launch {
                    runCatching {
                        action()
                        getComments(courseId)
                    }.onSuccess {
                        dispatch(
                            CourseCommentsReducerEvent.Refreshed(
                                comments = it.items,
                                nextCursor = it.nextCursor,
                                hasNext = it.hasNext,
                                clearDraft = clearDraft,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "댓글 쓰기 실패", e)
                        dispatch(
                            CourseCommentsReducerEvent.SubmitFailed(
                                (e as? IllegalArgumentException)?.message ?: "요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요.",
                            ),
                        )
                    }
                }
        }

        override fun reduce(
            state: CourseCommentsUIState,
            event: CourseCommentsReducerEvent,
        ): CourseCommentsUIState =
            when (event) {
                CourseCommentsReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is CourseCommentsReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        comments = event.comments.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                is CourseCommentsReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                CourseCommentsReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is CourseCommentsReducerEvent.MoreLoaded -> {
                    state.copy(
                        // 같은 댓글이 다시 와도 두 번 그리지 않는다(LazyColumn key 중복 크래시 방지).
                        comments = (state.comments + event.comments).distinctBy { it.id }.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                is CourseCommentsReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false, message = event.message)
                }

                is CourseCommentsReducerEvent.DraftChanged -> {
                    state.copy(draft = event.value)
                }

                CourseCommentsReducerEvent.SubmitStarted -> {
                    state.copy(isSubmitting = true, message = null)
                }

                is CourseCommentsReducerEvent.Refreshed -> {
                    state.copy(
                        isSubmitting = false,
                        comments = event.comments.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        draft = if (event.clearDraft) "" else state.draft,
                    )
                }

                is CourseCommentsReducerEvent.SubmitFailed -> {
                    state.copy(isSubmitting = false, message = event.message)
                }

                CourseCommentsReducerEvent.MessageConsumed -> {
                    state.copy(message = null)
                }
            }

        private companion object {
            const val TAG = "CourseComments"
        }
    }
