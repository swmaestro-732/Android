package com.chillsam.courmy.main.presentation.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.search.SearchCoursesUseCase
import com.chillsam.courmy.main.presentation.search.mock.SearchMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 검색 결과 화면(FS-18).
 *
 * 코스 탭은 [SearchCoursesUseCase](`GET /api/v1/courses/search`)로 실 조회한다.
 *
 * TODO(SCRUM-검색API): 장소 탭은 아직 [SearchMockData] 다 — 장소 검색 API(`GET /api/v1/places/search`)
 * 연동이 별도 작업이라 코스만 먼저 붙였다. 장소가 붙으면 mock 참조를 지운다.
 */
@HiltViewModel
class SearchResultViewModel
    @Inject
    constructor(
        private val searchCoursesUseCase: SearchCoursesUseCase,
    ) : MviViewModel<SearchResultIntent, SearchResultUIState, SearchResultReducerEvent>(
            SearchResultUIState.empty,
        ) {
        private var loadJob: Job? = null

        override fun onIntent(intent: SearchResultIntent) {
            when (intent) {
                is SearchResultIntent.Load -> {
                    // 같은 검색어로 재진입(구성 변경·복귀)하면 이미 띄운 결과를 그대로 둔다.
                    if (intent.query != currentState.query || currentState.places.isEmpty()) {
                        load(intent.query)
                    }
                    dispatch(SearchResultReducerEvent.TabSelected(intent.tab))
                }

                SearchResultIntent.Retry -> {
                    load(currentState.query)
                }

                is SearchResultIntent.SelectTab -> {
                    dispatch(SearchResultReducerEvent.TabSelected(intent.tab))
                }

                is SearchResultIntent.ToggleSave -> {
                    toggleSave(intent.placeId)
                }

                SearchResultIntent.ConsumeError -> {
                    dispatch(SearchResultReducerEvent.ErrorConsumed)
                }
            }
        }

        private fun load(query: String) {
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(SearchResultReducerEvent.LoadStarted(query))
                    // 서버가 빈 keyword 를 400 으로 거부한다. 라우트 인자가 빠진 경우라 빈 결과로 둔다.
                    if (query.isBlank()) {
                        dispatch(
                            SearchResultReducerEvent.Loaded(
                                places = emptyList(),
                                courses = emptyList(),
                                placeTotalCount = 0,
                                courseTotalCount = 0,
                            ),
                        )
                        return@launch
                    }
                    runCatching { searchCoursesUseCase(keyword = query) }
                        .onSuccess { courses ->
                            dispatch(
                                SearchResultReducerEvent.Loaded(
                                    // 장소 검색 API 연동 전까지는 목업 유지(파일 상단 TODO).
                                    places = SearchMockData.places,
                                    courses = courses,
                                    placeTotalCount = SearchMockData.PLACE_TOTAL_COUNT,
                                    // 응답에 전체 건수가 없어(커서 페이징) 첫 페이지 개수로 표기한다.
                                    courseTotalCount = courses.size,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "코스 검색 실패: query=$query", e)
                            dispatch(SearchResultReducerEvent.Failed("검색 결과를 불러오지 못했습니다."))
                        }
                }
        }

        private fun toggleSave(placeId: Long) {
            val saved = currentState.places.firstOrNull { it.id == placeId }?.saved ?: return
            dispatch(SearchResultReducerEvent.SaveToggled(placeId = placeId, saved = !saved))
        }

        override fun reduce(
            state: SearchResultUIState,
            event: SearchResultReducerEvent,
        ): SearchResultUIState =
            when (event) {
                is SearchResultReducerEvent.LoadStarted -> {
                    state.copy(query = event.query, isLoading = true, errorMessage = null)
                }

                is SearchResultReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        places = event.places.toImmutableList(),
                        courses = event.courses.toImmutableList(),
                        placeTotalCount = event.placeTotalCount,
                        courseTotalCount = event.courseTotalCount,
                    )
                }

                is SearchResultReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is SearchResultReducerEvent.TabSelected -> {
                    state.copy(selectedTab = event.tab)
                }

                is SearchResultReducerEvent.SaveToggled -> {
                    state.copy(
                        places =
                            state.places
                                .map { place ->
                                    if (place.id == event.placeId) place.copy(saved = event.saved) else place
                                }.toImmutableList(),
                    )
                }

                SearchResultReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private companion object {
            const val TAG = "SearchResult"
        }
    }
