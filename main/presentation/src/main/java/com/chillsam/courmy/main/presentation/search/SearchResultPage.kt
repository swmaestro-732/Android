package com.chillsam.courmy.main.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.search.SearchPage as SearchRoute

@Composable
fun SearchResultPage(
    query: String,
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(query) { viewModel.onIntent(SearchResultIntent.Load(query)) }
    SearchResultScreen(
        uiState = uiState,
        actions =
            SearchResultActions(
                onBack = { navigationHelper.navigateToBack() },
                onQueryClick = {
                    navigationHelper.navigateToBack()
                    navigationHelper.navigateByRoute(SearchRoute.route(query = query))
                },
                onClearQuery = {
                    navigationHelper.navigateToBack()
                    navigationHelper.navigateTo(SearchRoute)
                },
                onCourseClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(it.id)) },
                onRetry = { viewModel.onIntent(SearchResultIntent.Retry) },
                onLoadMore = { viewModel.onIntent(SearchResultIntent.LoadMore) },
            ),
        modifier = modifier,
    )
}
