package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.translation.catalogLabel
import com.chillsam.courmy.common.presentation.translation.courseTagLabel
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
import com.chillsam.courmy.main.presentation.R as MainR

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
            // 상단 여백은 정렬 버튼 줄이 이미 주므로 빼고, 바닥만 둔다(정렬 버튼과 첫 카드가 붙어 보이게).
            contentPadding =
                PaddingValues(
                    start = ScreenHorizontalPadding,
                    end = ScreenHorizontalPadding,
                    top = 2.dp,
                    bottom = 16.dp,
                ),
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
@Composable
private fun emptyMessage(uiState: SearchResultUIState): String =
    if (uiState.hasActiveFilter) {
        stringResource(MainR.string.main_search_result_empty_filtered)
    } else {
        stringResource(MainR.string.main_search_result_empty_query, uiState.query)
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
            DsChip(
                text = stringResource(MainR.string.main_search_result_category_all),
                selected = selected == null,
                onClick = { onSelect(null) },
            )
        }
        items(CourseTagVO.entries) { category ->
            DsChip(
                text = courseTagLabel(category),
                selected = selected == category,
                onClick = { onSelect(category) },
            )
        }
    }
}

/**
 * 정렬 선택. 목록 바로 위 오른쪽의 **⇅ 아이콘 + 테두리 버튼**(눌리는 컨트롤임이 보이게) 으로 두고,
 * 누르면 드롭다운에서 고른다. 기본은 관련순.
 */
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
            val shape = RoundedCornerShape(percent = 50)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier =
                    Modifier
                        .clip(shape)
                        .background(color.bgDefaultLevel1)
                        .border(1.dp, color.borderDefaultLevel1, shape)
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_swap_vert_24),
                    contentDescription = null,
                    tint = color.contentDefaultLevel1,
                    modifier = Modifier.size(13.dp),
                )
                DsText(
                    text = catalogLabel(sort.label),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SearchCourseSortVO.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            DsText(
                                text = catalogLabel(option.label),
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
