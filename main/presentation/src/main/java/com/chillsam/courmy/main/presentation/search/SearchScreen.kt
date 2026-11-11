package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.category.orCategoryLabel
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsBilingualText
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.translation.courseTagLabel
import com.chillsam.courmy.common.presentation.translation.rememberTaxonomyLabel
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.FeatureFlags
import com.chillsam.courmy.main.presentation.search.component.RecentKeywordChips
import com.chillsam.courmy.main.presentation.search.component.SearchInputBar
import com.chillsam.courmy.main.presentation.search.component.TrendingKeywordRow
import com.chillsam.courmy.main.presentation.R as MainR

/**
 * 검색어 입력 화면(FS-17)의 본문.
 *
 * 키보드가 올라온 채로 쓰는 화면이라 본문에 [imePadding] 을 걸어, 키보드에 가려 못 누르는 줄이
 * 생기지 않게 한다.
 */
@Composable
fun SearchScreen(
    uiState: SearchUIState,
    actions: SearchActions,
    placeholder: String = stringResource(MainR.string.main_search_placeholder),
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        SearchTopBar(
            query = uiState.query,
            placeholder = placeholder,
            onQueryChange = actions.onQueryChange,
            onSubmit = { actions.onSubmit(uiState.query, "keyboard") },
            onClear = actions.onClearQuery,
            onBack = actions.onBack,
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
        ) {
            if (uiState.placeSearch && uiState.query.isNotBlank()) {
                PlaceSuggestions(uiState, actions)
            }
            if (uiState.recentKeywords.isNotEmpty()) {
                SectionHeader(
                    title = stringResource(MainR.string.main_search_recent_title),
                    actionText = stringResource(MainR.string.main_search_clear_all),
                    onActionClick = actions.onClearAllRecent,
                )
                RecentKeywordChips(
                    keywords = uiState.recentKeywords,
                    onKeywordClick = { actions.onSubmit(it, "recent") },
                    onRemoveClick = actions.onRemoveRecent,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
                )
                Spacer(Modifier.height(28.dp))
            }
            if (uiState.trendingKeywords.isNotEmpty()) TrendingSection(uiState = uiState, actions = actions)
            // 검색어가 없어도 탐색을 시작할 수 있게 테마 바로가기를 항상 둔다(SCRUM-798).
            if (FeatureFlags.SEARCH_THEME_BROWSE_ENABLED && !uiState.placeSearch) {
                if (uiState.recentKeywords.isEmpty() && uiState.trendingKeywords.isEmpty()) {
                    Spacer(Modifier.height(20.dp))
                }
                ThemeFindSection(onThemeClick = actions.onThemeClick)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    placeholder: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left_24),
            contentDescription = stringResource(MainR.string.main_search_back),
            tint = color.contentDefaultLevel0,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
        )
        SearchInputBar(
            query = query,
            placeholder = placeholder,
            onQueryChange = onQueryChange,
            onSubmit = onSubmit,
            onClear = onClear,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TrendingSection(
    uiState: SearchUIState,
    actions: SearchActions,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding)
                .padding(top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(
            text = stringResource(MainR.string.main_search_trending_title),
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = uiState.trendingBaseTime,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel3,
        )
    }
    uiState.trendingKeywords.forEach { item ->
        TrendingKeywordRow(
            item = item,
            onClick = { actions.onSubmit(item.keyword, "trending") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
        )
    }
}

/** "테마로 찾기": 코스 태그 12종 칩. 검색어 없이도 테마 피드로 바로 가는 탐색 시작점(SCRUM-798). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeFindSection(onThemeClick: (String) -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    DsText(
        text = stringResource(MainR.string.main_search_theme_find_title),
        style = DesignSystemThemeImpl.typeScale.textStrongS,
        color = color.contentDefaultLevel0,
        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding).padding(bottom = 12.dp),
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CourseTagVO.entries.forEach { tag ->
            // 라우트 인자는 taxonomy 컨벤션대로 소문자 코드("date")를 싣는다. 라벨은 표시 전용.
            DsChip(text = courseTagLabel(tag), selected = false, onClick = { onThemeClick(tag.code) })
        }
    }
}

/** 구역 제목 + 오른쪽 보조 동작("전체 삭제"). */
@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding)
                .padding(top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = actionText,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel3,
            modifier =
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onActionClick,
                ),
        )
    }
}

@Composable
private fun PlaceSuggestions(
    state: SearchUIState,
    actions: SearchActions,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val message =
        when {
            state.suggestionsLoading -> stringResource(MainR.string.main_search_places_loading)
            state.suggestionsError != null -> state.suggestionsError
            state.suggestions.isEmpty() -> stringResource(MainR.string.main_search_places_empty)
            else -> null
        }
    message?.let {
        DsText(
            text = it,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            modifier = Modifier.padding(ScreenHorizontalPadding),
        )
    }
    state.suggestions.forEach { place ->
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { actions.onSuggestionClick(place) }
                .padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
        ) {
            DsBilingualText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = rememberTaxonomyLabel(place.detailCategory.orCategoryLabel(place.category)),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
            if (place.address.isNotBlank()) {
                DsBilingualText(
                    text = place.address,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
