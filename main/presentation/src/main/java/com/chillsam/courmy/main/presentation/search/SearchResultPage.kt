package com.chillsam.courmy.main.presentation.search

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.ProductEvent
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.search.SearchPage as SearchRoute

@Composable
fun SearchResultPage(
    query: String,
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = hiltViewModel(),
) {
    val telemetry = LocalTelemetry.current
    val select: (String, String) -> Unit = { filter, value ->
        telemetry.logEvent(
            ProductEvent.SelectionChange,
            mapOf(
                "screen" to "/search/result",
                "filter" to filter,
                "value" to value,
            ),
        )
    }
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(query) { viewModel.onIntent(SearchResultIntent.Load(query)) }
    // 상세에서 저장을 바꾸고 돌아와도 카드 북마크가 맞도록 복귀마다 동기화한다.
    RefreshOnResume { viewModel.onIntent(SearchResultIntent.SyncSavedIds) }
    // 저장 토글 실패는 화면을 바꾸지 않고 토스트로만 알린다(홈 피드와 동일).
    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(SearchResultIntent.ConsumeActionError)
        }
    }
    SearchResultScreen(
        uiState = uiState,
        actions =
            SearchResultActions(
                onBack = { navigationHelper.navigateToBack() },
                onQueryClick = {
                    navigationHelper.navigateToBack()
                    navigationHelper.navigateByRoute(
                        SearchRoute.route(query = query),
                        source = NavigationSource("/search/result", "edit_query"),
                    )
                },
                onClearQuery = {
                    navigationHelper.navigateToBack()
                    navigationHelper.navigateTo(SearchRoute, source = NavigationSource("/search/result", "clear_query"))
                },
                onCourseClick = {
                    telemetry.buttonClick("/search/result", "course", mapOf("course_id" to it.id))
                    navigationHelper.navigateByRoute(
                        CourseDetailPage.route(it.id),
                        source = NavigationSource("/search/result", "course"),
                    )
                },
                onToggleSave = {
                    telemetry.buttonClick("/search/result", "bookmark", mapOf("course_id" to it.id))
                    viewModel.onIntent(SearchResultIntent.ToggleSave(it.id))
                },
                onSelectArea = {
                    select("area", it ?: "all")
                    viewModel.onIntent(SearchResultIntent.SelectArea(it))
                },
                onSelectCategory = {
                    select("category", it?.name ?: "all")
                    viewModel.onIntent(SearchResultIntent.SelectCategory(it))
                },
                onSelectSort = {
                    select("sort", it.name)
                    viewModel.onIntent(SearchResultIntent.SelectSort(it))
                },
                onRetry = { viewModel.onIntent(SearchResultIntent.Retry) },
                onLoadMore = { viewModel.onIntent(SearchResultIntent.LoadMore) },
            ),
        modifier = modifier,
    )
}
