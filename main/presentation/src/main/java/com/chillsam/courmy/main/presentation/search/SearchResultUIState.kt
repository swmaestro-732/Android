package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.search.SearchCourseSortVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

data class SearchResultUIState(
    val query: String = "",
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val courses: ImmutableList<SearchCourseVO> = persistentListOf(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val errorMessage: String? = null,
    /** 지역 필터. 코스의 표시 지역명 그대로(예: "서울"). null 이면 전체. */
    val selectedArea: String? = null,
    /** 카테고리(코스 태그) 필터. null 이면 전체. 서버에는 enum 이름(대문자)으로 보낸다. */
    val selectedCategory: CourseTagVO? = null,
    val sort: SearchCourseSortVO = SearchCourseSortVO.RELEVANCE,
    /**
     * 지역 필터 옵션. 이번 검색어의 결과에 실려 온 지역명을 누적해 모은다 — 서버 area 필터가
     * 코스의 표시 지역명 그대로와 일치해야 동작해서(2026-11-07 실호출 확인) 고정 목록을 쓰지 않는다.
     */
    val areaOptions: ImmutableList<String> = persistentListOf(),
    /** 저장(북마크)된 코스 id. 카드 북마크 상태 표시용. */
    val savedCourseIds: ImmutableSet<String> = persistentSetOf(),
    /** 저장 토글 요청이 진행 중인 코스 id. 같은 코스의 중복 탭을 무시한다. */
    val savingCourseIds: ImmutableSet<String> = persistentSetOf(),
    /** 저장 토글 실패 등 화면을 바꾸지 않는 오류. 토스트로 보여 주고 소비한다. */
    val actionErrorMessage: String? = null,
) : UiState {
    val isBusy: Boolean
        get() = isLoading || isLoadingMore

    val canLoadMore: Boolean
        get() = !isBusy && hasNext && errorMessage == null

    /** 기본값이 아닌 필터가 하나라도 걸려 있는지(필터 칩 강조용). */
    val hasActiveFilter: Boolean
        get() = selectedArea != null || selectedCategory != null || sort != SearchCourseSortVO.RELEVANCE

    companion object {
        val empty = SearchResultUIState()
    }
}
