package com.chillsam.courmy.main.presentation.search

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.map.MapSearchResultPage
import com.chillsam.courmy.main.domain.search.SearchPage as SearchRoute

/** 검색 결과 화면(FS-18)의 라우팅 진입점. */
@Composable
fun SearchResultPage(
    query: String,
    tab: String,
    modifier: Modifier = Modifier,
    viewModel: SearchResultViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(query, tab) {
        viewModel.onIntent(
            SearchResultIntent.Load(query = query, tab = searchResultTabOf(tab)),
        )
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(SearchResultIntent.ConsumeError)
        }
    }

    val actions =
        remember(viewModel, navigationHelper) {
            SearchResultActions(
                onBack = { navigationHelper.navigateToBack() },
                // 검색어를 고치러 입력 화면으로 되돌아간다.
                onQueryClick = { navigationHelper.navigateTo(SearchRoute) },
                onClearQuery = { navigationHelper.navigateTo(SearchRoute) },
                onTabSelect = { viewModel.onIntent(SearchResultIntent.SelectTab(it)) },
                // TODO(SCRUM-검색API): 정렬 바꾸기는 서버가 정렬 파라미터를 받게 되면 붙인다.
                onSortClick = {},
                // TODO(SCRUM-장소상세): 장소 상세 라우트가 생기면 그쪽으로 넘긴다.
                onPlaceClick = {},
                onPlaceSaveToggle = { viewModel.onIntent(SearchResultIntent.ToggleSave(it)) },
                onCourseClick = { course ->
                    navigationHelper.navigateByRoute(CourseDetailPage.route(course.id))
                },
                onShowAllPlaces = {
                    viewModel.onIntent(SearchResultIntent.SelectTab(SearchResultTab.PLACE))
                },
                onShowAllCourses = {
                    viewModel.onIntent(SearchResultIntent.SelectTab(SearchResultTab.COURSE))
                },
                // 같은 검색어를 지도 화면으로 넘겨 결과를 지도 위에서 본다.
                onOpenMap = {
                    navigationHelper.navigateByRoute(MapSearchResultPage.route(query))
                },
            )
        }

    SearchResultScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier.fillMaxSize(),
    )
}
