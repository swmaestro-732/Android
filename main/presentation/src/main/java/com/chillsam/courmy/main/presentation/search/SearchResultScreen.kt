package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.home.FeedLoading
import com.chillsam.courmy.main.presentation.home.FeedMessage
import com.chillsam.courmy.main.presentation.search.component.SearchCourseCard
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
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (uiState.isLoading) {
                item { FeedLoading() }
            } else {
                items(uiState.courses, key = { it.id }) { course ->
                    SearchCourseCard(course = course, onClick = { actions.onCourseClick(course) })
                }
                if (uiState.errorMessage != null) {
                    item { FeedMessage(uiState.errorMessage, actions.onRetry) }
                } else if (uiState.courses.isEmpty()) {
                    item { FeedMessage("'${uiState.query}'에 맞는 코스가 없어요", null) }
                }
                if (uiState.isLoadingMore) item { LoadingMoreFooter() }
            }
        }
    }
}
