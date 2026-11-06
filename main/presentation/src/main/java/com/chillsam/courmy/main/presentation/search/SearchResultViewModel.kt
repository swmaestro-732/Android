package com.chillsam.courmy.main.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.format.formatCount
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.main.domain.saved.GetSavedCourseIdsUseCase
import com.chillsam.courmy.main.domain.search.SearchCoursesUseCase
import com.chillsam.courmy.main.entity.search.SearchCourseSortVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코스 검색 결과(FS-18) ViewModel.
 *
 * 필터(지역·카테고리·정렬)는 서버 파라미터로 보내 재조회한다 — area 는 코스의 표시 지역명 그대로,
 * category 는 enum 이름(대문자), sort 는 RELEVANCE·LATEST·POPULAR(2026-11-07 실호출 확인).
 * 저장(북마크)은 홈 피드와 같은 규칙: 서버가 확정한 뒤에 아이콘을 바꾸고, 실패는 토스트로만 알린다.
 */
@HiltViewModel
class SearchResultViewModel
    @Inject
    constructor(
        private val searchCoursesUseCase: SearchCoursesUseCase,
        private val getSavedCourseIdsUseCase: GetSavedCourseIdsUseCase,
        private val setCourseSavedUseCase: SetCourseSavedUseCase,
    ) : MviViewModel<SearchResultIntent, SearchResultUIState, SearchResultReducerEvent>(SearchResultUIState.empty) {
        private var loadJob: Job? = null

        override fun onIntent(intent: SearchResultIntent) {
            when (intent) {
                is SearchResultIntent.Load -> {
                    if (intent.query != currentState.query || currentState.isLoading) {
                        load(intent.query, resetFilters = intent.query != currentState.query)
                    }
                }

                SearchResultIntent.Retry -> {
                    load(currentState.query, append = currentState.courses.isNotEmpty())
                }

                SearchResultIntent.LoadMore -> {
                    if (currentState.canLoadMore) {
                        load(currentState.query, append = true)
                    }
                }

                is SearchResultIntent.SelectArea -> {
                    applyFilters(area = intent.area, category = currentState.selectedCategory, sort = currentState.sort)
                }

                is SearchResultIntent.SelectCategory -> {
                    applyFilters(area = currentState.selectedArea, category = intent.category, sort = currentState.sort)
                }

                is SearchResultIntent.SelectSort -> {
                    applyFilters(
                        area = currentState.selectedArea,
                        category = currentState.selectedCategory,
                        sort = intent.sort,
                    )
                }

                SearchResultIntent.SyncSavedIds -> {
                    syncSavedIds()
                }

                is SearchResultIntent.ToggleSave -> {
                    toggleSave(intent.courseId)
                }

                SearchResultIntent.ConsumeActionError -> {
                    dispatch(SearchResultReducerEvent.ActionErrorConsumed)
                }
            }
        }

        /** 필터가 실제로 바뀐 경우에만 반영하고 첫 페이지부터 다시 받는다. */
        private fun applyFilters(
            area: String?,
            category: CourseTagVO?,
            sort: SearchCourseSortVO,
        ) {
            val state = currentState
            if (state.selectedArea == area && state.selectedCategory == category && state.sort == sort) return
            dispatch(SearchResultReducerEvent.FilterApplied(area = area, category = category, sort = sort))
            load(state.query)
        }

        private fun load(
            query: String,
            append: Boolean = false,
            resetFilters: Boolean = false,
        ) {
            val cursor = if (append) currentState.nextCursor else null
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(SearchResultReducerEvent.LoadStarted(query, append, resetFilters))
                    val state = currentState
                    runCatching {
                        searchCoursesUseCase(
                            keyword = query,
                            cursor = cursor,
                            area = state.selectedArea,
                            category = state.selectedCategory?.name,
                            // 서버 기본값(RELEVANCE)은 파라미터 없이 보낸다.
                            sort = state.sort.takeIf { it != SearchCourseSortVO.RELEVANCE }?.name,
                        )
                    }.onSuccess { page -> dispatch(SearchResultReducerEvent.Loaded(page, append)) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            dispatch(
                                SearchResultReducerEvent.Failed(
                                    error.searchErrorMessage("코스 검색 결과를 불러오지 못했어요. 다시 시도해 주세요."),
                                ),
                            )
                        }
                }
        }

        /** 다른 화면에서 바뀐 저장 상태 동기화. 실패해도 화면을 막지 않는다(아이콘만 이전 상태 유지). */
        private fun syncSavedIds() {
            viewModelScope.launch {
                runCatching { getSavedCourseIdsUseCase() }
                    .onSuccess { dispatch(SearchResultReducerEvent.SavedIdsSynced(it)) }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "저장 상태 동기화 실패", e)
                    }
            }
        }

        /** 서버가 확정한 뒤에 아이콘을 바꾼다. 진행 중인 같은 코스의 중복 탭은 무시한다. */
        private fun toggleSave(courseId: String) {
            val state = currentState
            if (courseId in state.savingCourseIds) return
            val id = courseId.toLongOrNull() ?: return
            val nextSaved = courseId !in state.savedCourseIds
            dispatch(SearchResultReducerEvent.SaveStarted(courseId))
            viewModelScope.launch {
                runCatching { setCourseSavedUseCase(courseId = id, saved = nextSaved) }
                    .onSuccess { dispatch(SearchResultReducerEvent.SaveFinished(courseId, nextSaved)) }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "저장 변경 실패: courseId=$courseId", e)
                        dispatch(
                            SearchResultReducerEvent.SaveFailed(courseId, "저장을 변경하지 못했어요. 다시 시도해 주세요."),
                        )
                    }
            }
        }

        override fun reduce(
            state: SearchResultUIState,
            event: SearchResultReducerEvent,
        ): SearchResultUIState =
            when (event) {
                is SearchResultReducerEvent.LoadStarted -> {
                    reduceLoadStarted(state, event)
                }

                is SearchResultReducerEvent.Loaded -> {
                    val items = (if (event.append) state.courses else emptyList()) + event.page.items
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        courses = items.distinctBy { it.id }.toImmutableList(),
                        nextCursor = event.page.nextCursor,
                        hasNext = event.page.hasNext && event.page.nextCursor != state.nextCursor,
                        areaOptions =
                            (state.areaOptions + event.page.items.map { it.area })
                                .filter { it.isNotBlank() }
                                .distinct()
                                .toImmutableList(),
                    )
                }

                is SearchResultReducerEvent.Failed -> {
                    state.copy(isLoading = false, isLoadingMore = false, errorMessage = event.message)
                }

                is SearchResultReducerEvent.FilterApplied -> {
                    state.copy(selectedArea = event.area, selectedCategory = event.category, sort = event.sort)
                }

                else -> {
                    reduceSave(state, event)
                }
            }

        private fun reduceLoadStarted(
            state: SearchResultUIState,
            event: SearchResultReducerEvent.LoadStarted,
        ): SearchResultUIState =
            when {
                // 새 검색어: 필터·지역 옵션까지 초기화. 저장 상태는 화면 공통이라 유지한다.
                event.resetFilters -> {
                    SearchResultUIState(
                        query = event.query,
                        savedCourseIds = state.savedCourseIds,
                        savingCourseIds = state.savingCourseIds,
                    )
                }

                event.append -> {
                    state.copy(isLoadingMore = true, errorMessage = null)
                }

                // 필터 재검색: 선택·옵션·저장 상태를 지키고 목록만 비운다.
                else -> {
                    state.copy(
                        query = event.query,
                        isLoading = true,
                        isLoadingMore = false,
                        courses = persistentListOf(),
                        nextCursor = null,
                        hasNext = false,
                        errorMessage = null,
                    )
                }
            }

        /** 저장 토글 상태 변경. reduce 의 60줄 상한을 지키기 위해 분리한다(홈 피드와 같은 규칙). */
        private fun reduceSave(
            state: SearchResultUIState,
            event: SearchResultReducerEvent,
        ): SearchResultUIState =
            when (event) {
                is SearchResultReducerEvent.SavedIdsSynced -> {
                    state.copy(savedCourseIds = event.ids.toImmutableSet(), savedIdsKnown = true)
                }

                is SearchResultReducerEvent.SaveStarted -> {
                    state.copy(savingCourseIds = (state.savingCourseIds + event.courseId).toImmutableSet())
                }

                is SearchResultReducerEvent.SaveFinished -> {
                    val savedIds =
                        if (event.saved) {
                            state.savedCourseIds + event.courseId
                        } else {
                            state.savedCourseIds - event.courseId
                        }
                    state.copy(
                        // 카드의 저장 수도 함께 ±1 반영한다(홈 피드와 동일 규칙).
                        courses = updatedSaveCounts(state, event),
                        savedCourseIds = savedIds.toImmutableSet(),
                        savingCourseIds = (state.savingCourseIds - event.courseId).toImmutableSet(),
                    )
                }

                is SearchResultReducerEvent.SaveFailed -> {
                    state.copy(
                        savingCourseIds = (state.savingCourseIds - event.courseId).toImmutableSet(),
                        actionErrorMessage = event.message,
                    )
                }

                SearchResultReducerEvent.ActionErrorConsumed -> {
                    state.copy(actionErrorMessage = null)
                }

                else -> {
                    state
                }
            }

        /** 저장이 확정된 코스의 집계 숫자를 ±1 반영한다. 동기화 전이거나 원시 값이 없으면 건드리지 않는다. */
        private fun updatedSaveCounts(
            state: SearchResultUIState,
            event: SearchResultReducerEvent.SaveFinished,
        ) = state.courses
            .map { course ->
                if (course.id != event.courseId) return@map course
                val count = course.saveCount
                val wasSaved = course.id in state.savedCourseIds
                if (!state.savedIdsKnown || wasSaved == event.saved || count == null) {
                    course
                } else {
                    val updated =
                        (count.toLong() + if (event.saved) 1 else -1)
                            .coerceIn(0, Int.MAX_VALUE.toLong())
                            .toInt()
                    course.copy(saveCount = updated, saveCountText = formatCount(updated))
                }
            }.toImmutableList()

        private companion object {
            const val TAG = "SearchResult"
        }
    }
