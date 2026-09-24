package com.chillsam.courmy.main.presentation.map

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
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.main.presentation.map.mock.MapMockData

/**
 * 지도 검색 결과(FS-10-ResultMap / FS-10-List)의 라우팅 진입점.
 *
 * 하단 탭 소속이 아니라(검색에서 파고든 화면) 탭바를 붙이지 않는다.
 */
@Composable
fun MapSearchResultPage(
    query: String,
    listMode: Boolean,
    modifier: Modifier = Modifier,
    viewModel: MapSearchResultViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(query, listMode) {
        viewModel.onIntent(MapSearchResultIntent.Load(query.ifBlank { MapMockData.SAMPLE_QUERY }))
        // 지도 탭의 목록 버튼으로 들어오면 리스트가 펼쳐진 채로 시작한다.
        if (listMode) viewModel.onIntent(MapSearchResultIntent.ToggleSheetMode)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(MapSearchResultIntent.ConsumeError)
        }
    }

    val actions =
        remember(viewModel, navigationHelper) {
            MapSearchResultActions(
                onBackClick = { navigationHelper.navigateToBack() },
                // TODO(SCRUM-지도검색): 검색어 입력 화면이 붙으면 검색어를 들고 그쪽으로 되돌아간다.
                onQueryClick = {},
                onClearQuery = { navigationHelper.navigateToBack() },
                onRefinementSelect = { viewModel.onIntent(MapSearchResultIntent.SelectRefinement(it)) },
                onPlaceSelect = { viewModel.onIntent(MapSearchResultIntent.SelectPlace(it)) },
                onSaveToggle = { viewModel.onIntent(MapSearchResultIntent.ToggleSave(it)) },
                onSortSelect = { viewModel.onIntent(MapSearchResultIntent.ChangeSortOrder(it)) },
                onToggleSheetMode = { viewModel.onIntent(MapSearchResultIntent.ToggleSheetMode) },
                onSheetExpandedChange = {
                    viewModel.onIntent(MapSearchResultIntent.SetSheetExpanded(it))
                },
                // TODO(SCRUM-지도API): 지금은 코스 만들기 화면만 연다. 고른 장소를 담아 넘기는 건
                // 장소 식별자가 서버 값으로 바뀐 뒤에 붙인다.
                onAddToCourse = { navigationHelper.navigateTo(CourseCreatePage) },
            )
        }

    MapSearchResultScreen(
        uiState = uiState,
        actions = actions,
        modifier = modifier.fillMaxSize(),
    )
}
