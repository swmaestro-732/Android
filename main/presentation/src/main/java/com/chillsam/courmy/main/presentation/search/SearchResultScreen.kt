package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        // 카테고리는 상시 칩 줄(추천 코스와 동일 패턴), 정렬은 목록 위 오른쪽에 따로 둔다(SCRUM-795).
        CategoryChipRow(
            selected = uiState.selectedCategory,
            onSelect = actions.onSelectCategory,
        )
        SortSelectorRow(sort = uiState.sort, onSelect = actions.onSelectSort)
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

/** 카테고리(코스 태그) 칩 줄. 진입·선택 시 선택 칩이 보이도록 자동 스크롤한다(SCRUM-793과 동일 규칙). */
@Composable
private fun CategoryChipRow(
    selected: CourseTagVO?,
    onSelect: (CourseTagVO?) -> Unit,
) {
    val listState = rememberLazyListState()
    val selectedIndex = CourseTagVO.entries.indexOfFirst { it == selected } + 1
    LaunchedEffect(selectedIndex) {
        if (selectedIndex > 0) listState.animateScrollToItem(selectedIndex)
    }
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            DsChip(text = "전체", selected = selected == null, onClick = { onSelect(null) })
        }
        items(CourseTagVO.entries) { category ->
            DsChip(
                text = category.label,
                selected = selected == category,
                onClick = { onSelect(category) },
            )
        }
    }
}

/** 정렬 선택. 목록 바로 위 오른쪽에 "관련순 ▾" 꼴로 두고, 누르면 드롭다운에서 고른다. */
@Composable
private fun SortSelectorRow(
    sort: SearchCourseSortVO,
    onSelect: (SearchCourseSortVO) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var expanded by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Box {
            DsText(
                text = "${sort.label} ▾",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SearchCourseSortVO.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            DsText(
                                text = option.label,
                                style = DesignSystemThemeImpl.typeScale.textRegularS,
                                color =
                                    if (option == sort) color.contentAccent else color.contentDefaultLevel1,
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelect(option)
                        },
                    )
                }
            }
        }
    }
}
