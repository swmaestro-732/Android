package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.DeleteDraftUseCase
import com.chillsam.courmy.course.domain.GetDraftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 임시저장 목록 ViewModel.
 *
 * 초안이 기기가 아니라 서버에 있으므로 목록은 관찰이 아니라 진입할 때마다 조회한다.
 * 다른 기기나 작성 화면에서 만든 초안도 이 조회로 함께 들어온다.
 */
@HiltViewModel
class DraftListViewModel
    @Inject
    constructor(
        private val getDraftsUseCase: GetDraftsUseCase,
        private val deleteDraftUseCase: DeleteDraftUseCase,
    ) : MviViewModel<DraftListIntent, DraftListUIState, DraftListReducerEvent>(
            DraftListUIState.empty,
        ) {
        private var loadJob: Job? = null

        override fun onIntent(intent: DraftListIntent) {
            when (intent) {
                DraftListIntent.Load -> {
                    load()
                }

                is DraftListIntent.Delete -> {
                    delete(intent.courseId)
                }

                DraftListIntent.ConsumeError -> {
                    dispatch(DraftListReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun load() {
            loadJob?.cancel()
            dispatch(DraftListReducerEvent.LoadStarted)
            loadJob =
                viewModelScope.launch {
                    runCatching { getDraftsUseCase() }
                        .onSuccess { dispatch(DraftListReducerEvent.Loaded(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "임시저장 목록 조회 실패", e)
                            dispatch(DraftListReducerEvent.LoadFailed("임시저장한 코스를 불러오지 못했어요."))
                        }
                }
        }

        /**
         * 삭제는 서버 응답을 받고 나서 목록에서 뺀다. 먼저 지워 두면 실패했을 때 화면에는 사라졌는데
         * 서버에는 남아, 다시 들어왔을 때 되살아난 것처럼 보인다.
         */
        private fun delete(courseId: Long) {
            viewModelScope.launch {
                runCatching { deleteDraftUseCase(courseId) }
                    .onSuccess { dispatch(DraftListReducerEvent.Deleted(courseId)) }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "임시저장 삭제 실패: courseId=$courseId", e)
                        dispatch(DraftListReducerEvent.DeleteFailed("삭제하지 못했어요. 잠시 후 다시 시도해 주세요."))
                    }
            }
        }

        override fun reduce(
            state: DraftListUIState,
            event: DraftListReducerEvent,
        ): DraftListUIState =
            when (event) {
                DraftListReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, loadFailed = false, errorMessage = null)
                }

                is DraftListReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        loadFailed = false,
                        drafts = event.drafts.toImmutableList(),
                    )
                }

                is DraftListReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadFailed = true, errorMessage = event.message)
                }

                is DraftListReducerEvent.Deleted -> {
                    state.copy(drafts = state.drafts.filterNot { it.id == event.courseId }.toImmutableList())
                }

                is DraftListReducerEvent.DeleteFailed -> {
                    state.copy(errorMessage = event.message)
                }

                DraftListReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private companion object {
            const val TAG = "DraftList"
        }
    }
