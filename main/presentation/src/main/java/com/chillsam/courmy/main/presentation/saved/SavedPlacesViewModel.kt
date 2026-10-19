package com.chillsam.courmy.main.presentation.saved

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetPlaceSavedUseCase
import com.chillsam.courmy.main.domain.saved.GetSavedPlacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 저장함 · 장소 탭 ViewModel. `GET /service/v1/my/saved-places` 로 목록을 로드하고,
 * 저장 취소는 장소 상세 시트와 같은 [SetPlaceSavedUseCase] 를 쓴다(중복 구현 방지).
 * 구조는 코스 탭([SavedCoursesViewModel])과 동일하다.
 *
 * 로그인 사용자만 진입하는 화면이라(게스트는 별도 화면) JWT 가 있다고 전제한다.
 * 생성 시점에는 로드하지 않는다 — 화면이 RefreshOnResume 으로 부른다.
 */
@HiltViewModel
class SavedPlacesViewModel
    @Inject
    constructor(
        private val getSavedPlacesUseCase: GetSavedPlacesUseCase,
        private val setPlaceSavedUseCase: SetPlaceSavedUseCase,
    ) : MviViewModel<SavedPlacesIntent, SavedPlacesUIState, SavedPlacesReducerEvent>(
            SavedPlacesUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var moreJob: Job? = null
        private val unsaveJobs = mutableMapOf<Long, Job>()

        override fun onIntent(intent: SavedPlacesIntent) {
            when (intent) {
                is SavedPlacesIntent.SelectCategory -> {
                    dispatch(SavedPlacesReducerEvent.CategorySelected(intent.category))
                }

                is SavedPlacesIntent.SelectVisited -> {
                    dispatch(SavedPlacesReducerEvent.VisitedSelected(intent.visited))
                }

                SavedPlacesIntent.Load,
                SavedPlacesIntent.Retry,
                -> {
                    load()
                }

                SavedPlacesIntent.LoadMore -> {
                    loadMore()
                }

                is SavedPlacesIntent.Unsave -> {
                    unsave(intent.placeId)
                }

                SavedPlacesIntent.ConsumeError -> {
                    dispatch(SavedPlacesReducerEvent.ErrorConsumed)
                }
            }
        }

        override fun reduce(
            state: SavedPlacesUIState,
            event: SavedPlacesReducerEvent,
        ): SavedPlacesUIState =
            when (event) {
                is SavedPlacesReducerEvent.CategorySelected -> {
                    state.copy(selectedCategory = event.category)
                }

                is SavedPlacesReducerEvent.VisitedSelected -> {
                    state.copy(showVisited = event.visited)
                }

                SavedPlacesReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, loadErrorMessage = null)
                }

                is SavedPlacesReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        places = event.places.toImmutableList(),
                        loadErrorMessage = null,
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                SavedPlacesReducerEvent.LoadMoreStarted -> {
                    state.copy(isLoadingMore = true)
                }

                is SavedPlacesReducerEvent.MoreLoaded -> {
                    state.copy(
                        // 서버가 같은 장소를 다시 줘도 두 번 그리지 않는다(key 중복 방지).
                        places = (state.places + event.places).distinctBy { it.placeId }.toImmutableList(),
                        nextCursor = event.nextCursor,
                        hasNext = event.hasNext,
                        isLoadingMore = false,
                    )
                }

                is SavedPlacesReducerEvent.MoreFailed -> {
                    state.copy(isLoadingMore = false, errorMessage = event.message)
                }

                is SavedPlacesReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadErrorMessage = event.message)
                }

                is SavedPlacesReducerEvent.Unsaved -> {
                    state.copy(
                        places = state.places.filterNot { it.placeId == event.placeId }.toImmutableList(),
                    )
                }

                is SavedPlacesReducerEvent.UnsaveFailed -> {
                    state.copy(errorMessage = event.message)
                }

                SavedPlacesReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private fun load() {
            dispatch(SavedPlacesReducerEvent.LoadStarted)
            loadJob?.cancel()
            // 진행 중인 이어받기를 끊는다. 안 끊으면 뒤늦게 도착한 옛 페이지가 새 목록 뒤에 붙는다.
            moreJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getSavedPlacesUseCase() }
                        .onSuccess { page ->
                            dispatch(
                                SavedPlacesReducerEvent.Loaded(
                                    places = page.items,
                                    nextCursor = page.nextCursor,
                                    hasNext = page.hasNext,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "저장 장소 로드 실패", e)
                            dispatch(SavedPlacesReducerEvent.LoadFailed("저장한 장소를 불러오지 못했습니다."))
                        }
                }
        }

        /** 다음 페이지를 이어 받는다. 첫 로드 전이거나 마지막 페이지면 아무것도 하지 않는다. */
        private fun loadMore() {
            val state = currentState
            if (state.isLoading || state.isLoadingMore) return
            val cursor = state.nextCursor?.takeIf { state.hasNext } ?: return
            dispatch(SavedPlacesReducerEvent.LoadMoreStarted)
            moreJob?.cancel()
            moreJob =
                viewModelScope.launch {
                    runCatching { getSavedPlacesUseCase(cursor = cursor) }
                        .onSuccess { page ->
                            dispatch(
                                SavedPlacesReducerEvent.MoreLoaded(
                                    places = page.items,
                                    nextCursor = page.nextCursor,
                                    hasNext = page.hasNext,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "저장 장소 다음 페이지 로드 실패", e)
                            dispatch(SavedPlacesReducerEvent.MoreFailed("더 불러오지 못했어요."))
                        }
                }
        }

        /**
         * 서버가 성공을 확인한 뒤에만 목록에서 뺀다(코스 탭과 같은 판단 — 실패가 잦은 초기 연동에서
         * 낙관적 제거는 화면이 튀어 보이기 쉽다). 장소마다 독립된 요청이라 서로 취소하지 않는다.
         */
        private fun unsave(placeId: Long) {
            if (placeId <= 0L) {
                Log.w(TAG, "잘못된 장소 id: $placeId")
                dispatch(SavedPlacesReducerEvent.UnsaveFailed("저장을 취소하지 못했어요."))
                return
            }
            // 같은 장소의 중복 탭만 무시한다.
            if (unsaveJobs[placeId]?.isActive == true) return
            unsaveJobs[placeId] =
                viewModelScope.launch {
                    try {
                        runCatching { setPlaceSavedUseCase(placeId = placeId, saved = false) }
                            .onSuccess { dispatch(SavedPlacesReducerEvent.Unsaved(placeId)) }
                            .onFailure { e ->
                                if (e is CancellationException) throw e
                                Log.w(TAG, "저장 취소 실패: placeId=$placeId", e)
                                dispatch(SavedPlacesReducerEvent.UnsaveFailed("저장을 취소하지 못했어요."))
                            }
                    } finally {
                        unsaveJobs.remove(placeId)
                    }
                }
        }

        private companion object {
            const val TAG = "SavedPlaces"
        }
    }
