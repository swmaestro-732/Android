package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.search.SearchResultPage
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** 검색 결과의 상단 탭(FS-18). */
enum class SearchResultTab {
    ALL,
    PLACE,
    COURSE,
}

/** 탭 이름. 화면 문구라 presentation 에 둔다. */
internal fun SearchResultTab.label(): String =
    when (this) {
        SearchResultTab.ALL -> "전체"
        SearchResultTab.PLACE -> "장소"
        SearchResultTab.COURSE -> "코스"
    }

/** 라우트 인자 문자열 ↔ 탭. 모르는 값이 오면 전체로 떨어뜨린다(딥링크로 아무 값이나 들어올 수 있다). */
internal fun searchResultTabOf(raw: String?): SearchResultTab =
    when (raw) {
        SearchResultPage.TAB_PLACE -> SearchResultTab.PLACE
        SearchResultPage.TAB_COURSE -> SearchResultTab.COURSE
        else -> SearchResultTab.ALL
    }

/**
 * 검색 결과 화면(FS-18)의 상태.
 *
 * 장소는 지도 탭과 같은 [MapPlaceVO] 를 쓴다 — 두 화면의 장소 줄이 보여주는 값이 같아서
 * 같은 것을 두 벌로 두지 않는다.
 */
data class SearchResultUIState(
    val query: String = "",
    val isLoading: Boolean = true,
    val selectedTab: SearchResultTab = SearchResultTab.ALL,
    val places: ImmutableList<MapPlaceVO> = persistentListOf(),
    val courses: ImmutableList<SearchCourseVO> = persistentListOf(),
    /** 검색어에 걸린 전체 장소 수. 목록에 실린 개수와 다를 수 있다. */
    val placeTotalCount: Int = 0,
    val courseTotalCount: Int = 0,
    val errorMessage: String? = null,
) : UiState {
    /** 전체 탭에서 미리 보여줄 장소. 나머지는 "장소 모두 보기"로 넘긴다. */
    val previewPlaces: List<MapPlaceVO>
        get() = places.take(ALL_TAB_PREVIEW_PLACES)

    /** 전체 탭의 "관련 코스"에 보여줄 코스. */
    val previewCourses: List<SearchCourseVO>
        get() = courses.take(ALL_TAB_PREVIEW_COURSES)

    val isEmpty: Boolean
        get() = !isLoading && places.isEmpty() && courses.isEmpty()

    companion object {
        private const val ALL_TAB_PREVIEW_PLACES = 3
        private const val ALL_TAB_PREVIEW_COURSES = 2

        val empty: SearchResultUIState = SearchResultUIState()
    }
}
