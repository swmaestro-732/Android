package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.domain.telemetry.searchSubmit
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.main.domain.home.RecommendedCoursesPage
import com.chillsam.courmy.main.domain.map.MapSearchResultPage
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.presentation.R
import com.chillsam.courmy.main.domain.search.SearchPage as SearchRoute
import com.chillsam.courmy.main.domain.search.SearchResultPage as SearchResultRoute

/**
 * 검색어 입력 화면(FS-17)의 라우팅 진입점.
 *
 * 검색을 실행하면 결과 화면을 **덧쌓지 않고 갈아 끼운다**. 남겨 두면 결과에서 뒤로 눌렀을 때
 * 입력 화면이 다시 나오고, 거기서 또 뒤로 눌러야 홈으로 가서 뒤로가기를 두 번 하게 된다.
 */
@Composable
fun SearchPage(
    target: String = SearchRoute.TARGET_COURSE,
    query: String = "",
    latitude: Double? = null,
    longitude: Double? = null,
    bounds: MapBounds? = null,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val telemetry = LocalTelemetry.current
    val navigationHelper = LocalNavigationHelper.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(query, target) {
        viewModel.onIntent(SearchIntent.Load(target == SearchRoute.TARGET_PLACE))
        viewModel.onIntent(SearchIntent.ChangeQuery(query))
    }

    val actions =
        remember(viewModel, navigationHelper, telemetry, target, latitude, longitude, bounds) {
            SearchActions(
                onQueryChange = { viewModel.onIntent(SearchIntent.ChangeQuery(it)) },
                onClearQuery = {
                    telemetry.buttonClick(SearchRoute.PATH, "clear_query")
                    viewModel.onIntent(SearchIntent.ClearQuery)
                },
                // 테마 칩: 홈 테마 타일과 같은 목적지(추천 코스의 해당 카테고리 필터)로 보낸다.
                onThemeClick = { code ->
                    telemetry.buttonClick(SearchRoute.PATH, "theme", mapOf("theme" to code))
                    navigationHelper.navigateToBack()
                    navigationHelper.navigateByRoute(
                        RecommendedCoursesPage.route(code),
                        source = NavigationSource("/search", "theme"),
                    )
                },
                onRemoveRecent = {
                    telemetry.buttonClick(SearchRoute.PATH, "remove_recent")
                    viewModel.onIntent(SearchIntent.RemoveRecent(it))
                },
                onClearAllRecent = {
                    telemetry.buttonClick(SearchRoute.PATH, "clear_all_recent")
                    viewModel.onIntent(SearchIntent.ClearAllRecent)
                },
                onSubmit = { keyword, source ->
                    val trimmed = keyword.trim()
                    if (trimmed.isNotEmpty()) {
                        telemetry.searchSubmit(trimmed, target, source)
                        viewModel.onIntent(SearchIntent.Submit(trimmed))
                        navigationHelper.navigateToBack()
                        navigationHelper.navigateByRoute(
                            if (target == SearchRoute.TARGET_PLACE) {
                                MapSearchResultPage.route(
                                    trimmed,
                                    latitude = latitude,
                                    longitude = longitude,
                                    bounds = bounds,
                                )
                            } else {
                                SearchResultRoute.route(trimmed)
                            },
                            source = NavigationSource("/search", "search_$source"),
                        )
                    }
                },
                onBack = { navigationHelper.navigateToBack() },
                onSuggestionClick = { place ->
                    val keyword =
                        viewModel.uiState.value.query
                            .trim()
                    telemetry.searchSubmit(keyword, target, "place_suggestion")
                    viewModel.onIntent(SearchIntent.Submit(keyword))
                    navigationHelper.navigateToBack()
                    navigationHelper.navigateByRoute(
                        MapSearchResultPage.route(
                            keyword,
                            latitude = latitude,
                            longitude = longitude,
                            bounds = bounds,
                            targetLatitude = place.latitude,
                            targetLongitude = place.longitude,
                        ),
                        source = NavigationSource("/search", "place_suggestion"),
                    )
                },
            )
        }

    SearchScreen(
        uiState = uiState,
        placeholder =
            if (target == SearchRoute.TARGET_PLACE) {
                stringResource(R.string.main_search_placeholder_place)
            } else {
                stringResource(R.string.main_search_placeholder_course)
            },
        actions = actions,
        modifier = modifier.fillMaxSize(),
    )
}
