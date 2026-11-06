package com.chillsam.courmy.main.presentation.search

import androidx.compose.runtime.Immutable
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.search.SearchCourseSortVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO

/** 검색어 입력 화면(FS-17)이 상위로 올려 보내는 동작 묶음. */
@Immutable
data class SearchActions(
    val onQueryChange: (String) -> Unit,
    val onClearQuery: () -> Unit,
    val onRemoveRecent: (String) -> Unit,
    val onClearAllRecent: () -> Unit,
    /** 검색 실행. 최근 검색어 갱신과 결과 화면 이동을 함께 처리한다. */
    val onSubmit: (String) -> Unit,
    val onBack: () -> Unit,
    val onSuggestionClick: (MapPlaceVO) -> Unit = {},
    /** "테마로 찾기" 칩 — 코스 태그 소문자 코드를 싣는다(홈 테마 타일과 동일 목적지). */
    val onThemeClick: (String) -> Unit = {},
)

/** 검색 결과 화면(FS-18)이 상위로 올려 보내는 동작 묶음. */
@Immutable
data class SearchResultActions(
    val onBack: () -> Unit,
    val onSuggestionClick: (MapPlaceVO) -> Unit = {},
    /** 검색어 바 탭 — 검색어를 고치러 입력 화면으로 되돌아간다. */
    val onQueryClick: () -> Unit,
    val onClearQuery: () -> Unit,
    val onCourseClick: (SearchCourseVO) -> Unit,
    val onToggleSave: (SearchCourseVO) -> Unit,
    val onSelectArea: (String?) -> Unit,
    val onSelectCategory: (CourseTagVO?) -> Unit,
    val onSelectSort: (SearchCourseSortVO) -> Unit,
    val onRetry: () -> Unit,
    val onLoadMore: () -> Unit,
)
