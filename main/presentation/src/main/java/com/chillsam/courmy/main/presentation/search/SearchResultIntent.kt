package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.main.entity.search.SearchCourseSortVO

sealed interface SearchResultIntent : MviIntent {
    data class Load(
        val query: String,
    ) : SearchResultIntent

    data object Retry : SearchResultIntent

    data object LoadMore : SearchResultIntent

    /** 지역 필터 선택. null 이면 전체. 같은 값을 다시 고르면 무시한다. */
    data class SelectArea(
        val area: String?,
    ) : SearchResultIntent

    /** 카테고리(코스 태그) 필터 선택. null 이면 전체. */
    data class SelectCategory(
        val category: CourseTagVO?,
    ) : SearchResultIntent

    data class SelectSort(
        val sort: SearchCourseSortVO,
    ) : SearchResultIntent

    /** 다른 화면에서 바뀐 저장 상태 동기화. 화면 복귀마다 부른다. */
    data object SyncSavedIds : SearchResultIntent

    /** 카드 북마크 토글. 서버 확정 후 아이콘을 바꾼다. */
    data class ToggleSave(
        val courseId: String,
    ) : SearchResultIntent

    data object ConsumeActionError : SearchResultIntent
}
