package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.main.domain.search.SearchResultPage as SearchResultRoute

/**
 * 검색어 입력 화면(FS-17)의 라우팅 진입점.
 *
 * 검색을 실행하면 결과 화면을 **덧쌓지 않고 갈아 끼운다**. 남겨 두면 결과에서 뒤로 눌렀을 때
 * 입력 화면이 다시 나오고, 거기서 또 뒤로 눌러야 홈으로 가서 뒤로가기를 두 번 하게 된다.
 */
@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onIntent(SearchIntent.Load) }

    val actions =
        remember(viewModel, navigationHelper) {
            SearchActions(
                onQueryChange = { viewModel.onIntent(SearchIntent.ChangeQuery(it)) },
                onClearQuery = { viewModel.onIntent(SearchIntent.ClearQuery) },
                onRemoveRecent = { viewModel.onIntent(SearchIntent.RemoveRecent(it)) },
                onClearAllRecent = { viewModel.onIntent(SearchIntent.ClearAllRecent) },
                onSubmit = { keyword ->
                    val trimmed = keyword.trim()
                    if (trimmed.isNotEmpty()) {
                        viewModel.onIntent(SearchIntent.Submit(trimmed))
                        navigationHelper.navigateToBack()
                        navigationHelper.navigateByRoute(SearchResultRoute.route(trimmed))
                    }
                },
                onBack = { navigationHelper.navigateToBack() },
            )
        }

    SearchScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier.fillMaxSize(),
    )
}
