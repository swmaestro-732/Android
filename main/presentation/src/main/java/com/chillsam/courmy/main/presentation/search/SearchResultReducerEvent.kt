package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.search.SearchCourseSortVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO

sealed interface SearchResultReducerEvent : ReducerEvent {
    /** [resetFilters] 는 새 검색어 진입에만 true — 필터 재검색에서는 선택·지역 옵션·저장 상태를 지키려 false. */
    data class LoadStarted(
        val query: String,
        val append: Boolean,
        val resetFilters: Boolean,
    ) : SearchResultReducerEvent

    data class Loaded(
        val page: CursorPageVO<SearchCourseVO>,
        val append: Boolean,
    ) : SearchResultReducerEvent

    data class Failed(
        val message: String,
    ) : SearchResultReducerEvent

    /** 필터 선택 반영. 목록 재조회는 ViewModel 이 이어서 수행한다. */
    data class FilterApplied(
        val area: String?,
        val category: CourseTagVO?,
        val sort: SearchCourseSortVO,
    ) : SearchResultReducerEvent

    data class SavedIdsSynced(
        val ids: Set<String>,
    ) : SearchResultReducerEvent

    data class SaveStarted(
        val courseId: String,
    ) : SearchResultReducerEvent

    data class SaveFinished(
        val courseId: String,
        val saved: Boolean,
    ) : SearchResultReducerEvent

    data class SaveFailed(
        val courseId: String,
        val message: String,
    ) : SearchResultReducerEvent

    data object ActionErrorConsumed : SearchResultReducerEvent
}
