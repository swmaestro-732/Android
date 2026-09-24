package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.search.component.RecentKeywordChips
import com.chillsam.courmy.main.presentation.search.component.SearchInputBar
import com.chillsam.courmy.main.presentation.search.component.TrendingKeywordRow

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
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        SearchTopBar(
            query = uiState.query,
            onQueryChange = actions.onQueryChange,
            onSubmit = { actions.onSubmit(uiState.query) },
            onClear = actions.onClearQuery,
            onBack = actions.onBack,
            onCancel = actions.onBack,
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
        ) {
            if (uiState.recentKeywords.isNotEmpty()) {
                SectionHeader(
                    title = "최근 검색",
                    actionText = "전체 삭제",
                    onActionClick = actions.onClearAllRecent,
                )
                RecentKeywordChips(
                    keywords = uiState.recentKeywords,
                    onKeywordClick = actions.onSubmit,
                    onRemoveClick = actions.onRemoveRecent,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
                )
                Spacer(Modifier.height(28.dp))
            }
            TrendingSection(uiState = uiState, actions = actions)
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
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
            contentDescription = "뒤로",
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
            onQueryChange = onQueryChange,
            onSubmit = onSubmit,
            onClear = onClear,
            modifier = Modifier.weight(1f),
        )
        DsText(
            text = "취소",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel1,
            modifier =
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCancel,
                ),
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
            text = "지금 뜨는 검색어",
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
            onClick = { actions.onSubmit(item.keyword) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
        )
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
