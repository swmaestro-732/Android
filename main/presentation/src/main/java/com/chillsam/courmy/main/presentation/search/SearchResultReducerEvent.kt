package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.search.SearchCourseVO

sealed interface SearchResultReducerEvent : ReducerEvent {
    data class LoadStarted(
        val query: String,
        val append: Boolean,
    ) : SearchResultReducerEvent

    data class Loaded(
        val page: CursorPageVO<SearchCourseVO>,
        val append: Boolean,
    ) : SearchResultReducerEvent

    data class Failed(
        val message: String,
    ) : SearchResultReducerEvent
}
