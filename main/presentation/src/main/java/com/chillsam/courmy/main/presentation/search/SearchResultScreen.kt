package com.chillsam.courmy.main.presentation.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.search.SearchCourseSortVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import com.chillsam.courmy.main.presentation.component.CourseCardsSkeleton
import com.chillsam.courmy.main.presentation.component.CourseSkeletonLayout
import com.chillsam.courmy.main.presentation.component.HomeCourseCard
import com.chillsam.courmy.main.presentation.home.FeedMessage
import com.chillsam.courmy.main.presentation.search.component.SearchResultTopBar

@Composable
fun SearchResultScreen(
    uiState: SearchResultUIState,
    actions: SearchResultActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val listState = rememberLazyListState()
    LoadMoreOnScrollEnd(listState, onLoadMore = actions.onLoadMore)
    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        SearchResultTopBar(
            query = uiState.query,
            onBack = actions.onBack,
            onQueryClick = actions.onQueryClick,
            onClear = actions.onClearQuery,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp),
        )
        SearchFilterSection(uiState = uiState, actions = actions)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.isLoading) {
                item { CourseCardsSkeleton(CourseSkeletonLayout.SEARCH) }
            } else {
                items(uiState.courses, key = { it.id }) { course ->
                    // 홈 피드와 같은 카드(커버 제목 오버레이·카테고리 칩·북마크 토글)로 통일한다(SCRUM-794).
                    HomeCourseCard(
                        course = course.toHomeCard(),
                        isSaved = course.id in uiState.savedCourseIds,
                        onClick = { actions.onCourseClick(course) },
                        onBookmarkClick = { actions.onToggleSave(course) },
                    )
                }
                if (uiState.errorMessage != null) {
                    item { FeedMessage(uiState.errorMessage, actions.onRetry) }
                } else if (uiState.courses.isEmpty()) {
                    item { FeedMessage(emptyMessage(uiState), null) }
                }
                if (uiState.isLoadingMore) item { LoadingMoreFooter() }
            }
        }
    }
}

/** 필터가 걸려 있으면 "조건에 맞는" 문구로 바꿔 검색어 문제가 아님을 알린다. */
private fun emptyMessage(uiState: SearchResultUIState): String =
    if (uiState.hasActiveFilter) {
        "조건에 맞는 코스가 없어요. 필터를 조정해 보세요."
    } else {
        "'${uiState.query}'에 맞는 코스가 없어요"
    }

/** 검색 카드가 홈 카드와 같은 모양으로 그려지도록 표시 필드만 옮긴다. */
private fun SearchCourseVO.toHomeCard(): HomeCourseVO =
    HomeCourseVO(
        id = id,
        title = title,
        coverImageUrl = coverImageUrl,
        categoryLabel = categoryLabel,
        saveCountText = saveCountText,
        likeCountText = likeCountText,
    )

/**
 * 필터 줄(SCRUM-794). "필터" 칩을 누르면 정렬·지역·카테고리 선택이 펼쳐진다.
 * 선택 즉시 재검색하고, 지역 옵션은 이번 검색어의 결과에서 모은 지역명이다(서버 area 가 표시명 그대로라서).
 */
@Composable
private fun SearchFilterSection(
    uiState: SearchResultUIState,
    actions: SearchResultActions,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsChip(
                text = if (uiState.hasActiveFilter) "필터 ${activeFilterCount(uiState)}" else "필터",
                selected = expanded || uiState.hasActiveFilter,
                onClick = { expanded = !expanded },
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FilterChipRow(label = "정렬") {
                    items(SearchCourseSortVO.entries) { sort ->
                        DsChip(
                            text = sort.label,
                            selected = uiState.sort == sort,
                            onClick = { actions.onSelectSort(sort) },
                        )
                    }
                }
                FilterChipRow(label = "지역") {
                    item {
                        DsChip(
                            text = "전체",
                            selected = uiState.selectedArea == null,
                            onClick = { actions.onSelectArea(null) },
                        )
                    }
                    items(uiState.areaOptions) { area ->
                        DsChip(
                            text = area,
                            selected = uiState.selectedArea == area,
                            onClick = { actions.onSelectArea(area) },
                        )
                    }
                }
                FilterChipRow(label = "카테고리") {
                    item {
                        DsChip(
                            text = "전체",
                            selected = uiState.selectedCategory == null,
                            onClick = { actions.onSelectCategory(null) },
                        )
                    }
                    items(CourseTagVO.entries) { category ->
                        DsChip(
                            text = category.label,
                            selected = uiState.selectedCategory == category,
                            onClick = { actions.onSelectCategory(category) },
                        )
                    }
                }
            }
        }
    }
}

/** 기본값이 아닌 필터 수(필터 칩 라벨에 붙인다). */
private fun activeFilterCount(uiState: SearchResultUIState): Int =
    listOf(
        uiState.selectedArea != null,
        uiState.selectedCategory != null,
        uiState.sort != SearchCourseSortVO.RELEVANCE,
    ).count { it }

/** 라벨 + 가로 스크롤 칩 한 줄. */
@Composable
private fun FilterChipRow(
    label: String,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}
