package com.chillsam.courmy.main.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.search.component.SearchCourseCard
import com.chillsam.courmy.main.presentation.search.component.SearchCourseCompactCard
import com.chillsam.courmy.main.presentation.search.component.SearchMapPreviewCard
import com.chillsam.courmy.main.presentation.search.component.SearchPlaceRow
import com.chillsam.courmy.main.presentation.search.component.SearchResultSectionBar
import com.chillsam.courmy.main.presentation.search.component.SearchResultTopBar
import com.chillsam.courmy.main.presentation.search.component.SearchShowAllButton
import com.chillsam.courmy.main.presentation.search.component.SearchTabChip

/** 장소 탭의 정렬 기준. 정렬 바꾸기는 아직 없어 표시만 한다. */
private const val PLACE_SORT_LABEL = "거리순"

/** 코스 탭의 정렬 기준. */
private const val COURSE_SORT_LABEL = "인기순"

/**
 * 검색 결과 화면(FS-18)의 본문.
 *
 * 전체·장소·코스 세 탭이 상단바를 공유하고 목록만 달라진다. 목록이 길어질 수 있어 통째로
 * [LazyColumn] 하나에 싣고, 탭마다 넣는 항목만 바꾼다.
 */
@Composable
fun SearchResultScreen(
    uiState: SearchResultUIState,
    actions: SearchResultActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        SearchResultTopBar(
            query = uiState.query,
            onBack = actions.onBack,
            onQueryClick = actions.onQueryClick,
            onClear = actions.onClearQuery,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenHorizontalPadding)
                    .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SearchResultTab.entries.forEach { tab ->
                SearchTabChip(
                    text = tab.label(),
                    selected = tab == uiState.selectedTab,
                    onClick = { actions.onTabSelect(tab) },
                )
            }
        }

        if (uiState.isEmpty) {
            EmptyResults(query = uiState.query, modifier = Modifier.weight(1f))
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding =
                PaddingValues(
                    start = ScreenHorizontalPadding,
                    end = ScreenHorizontalPadding,
                    top = 8.dp,
                    bottom = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (uiState.selectedTab) {
                SearchResultTab.ALL -> allTabItems(uiState, actions)
                SearchResultTab.PLACE -> placeTabItems(uiState, actions)
                SearchResultTab.COURSE -> courseTabItems(uiState, actions)
            }
        }
    }
}

/** 전체 탭: 장소 미리보기 + 지도 입구 + 관련 코스. 각 구역 끝에 "모두 보기"로 해당 탭을 연다. */
private fun LazyListScope.allTabItems(
    uiState: SearchResultUIState,
    actions: SearchResultActions,
) {
    item(key = "place-section") {
        SearchResultSectionBar(
            title = "장소",
            sortLabel = PLACE_SORT_LABEL,
            onSortClick = actions.onSortClick,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
    item(key = "map-preview") {
        SearchMapPreviewCard(
            placeCount = uiState.placeTotalCount,
            onClick = actions.onOpenMap,
        )
    }
    items(uiState.previewPlaces, key = { "place-${it.id}" }) { place ->
        SearchPlaceRow(
            place = place,
            onClick = { actions.onPlaceClick(place.id) },
            onSaveClick = { actions.onPlaceSaveToggle(place.id) },
        )
    }
    item(key = "place-more") {
        SearchShowAllButton(text = "장소 모두 보기", onClick = actions.onShowAllPlaces)
    }
    item(key = "course-section") {
        SectionTitle(text = "관련 코스", modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
    }
    items(uiState.previewCourses, key = { "course-${it.id}" }) { course ->
        SearchCourseCompactCard(course = course, onClick = { actions.onCourseClick(course) })
    }
    item(key = "course-more") {
        SearchShowAllButton(text = "코스 모두 보기", onClick = actions.onShowAllCourses)
    }
}

/** 장소 탭: 걸린 장소 전부를 한 줄씩. */
private fun LazyListScope.placeTabItems(
    uiState: SearchResultUIState,
    actions: SearchResultActions,
) {
    item(key = "place-section") {
        SearchResultSectionBar(
            title = "장소 ${uiState.placeTotalCount}곳",
            sortLabel = PLACE_SORT_LABEL,
            onSortClick = actions.onSortClick,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
    items(uiState.places, key = { "place-${it.id}" }) { place ->
        SearchPlaceRow(
            place = place,
            onClick = { actions.onPlaceClick(place.id) },
            onSaveClick = { actions.onPlaceSaveToggle(place.id) },
        )
    }
}

/** 코스 탭: 커버가 큰 카드로 코스를 쭉 보여준다. */
private fun LazyListScope.courseTabItems(
    uiState: SearchResultUIState,
    actions: SearchResultActions,
) {
    item(key = "course-section") {
        SearchResultSectionBar(
            title = "코스 ${uiState.courseTotalCount}개",
            sortLabel = COURSE_SORT_LABEL,
            onSortClick = actions.onSortClick,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
    items(uiState.courses, key = { "course-${it.id}" }) { course ->
        SearchCourseCard(course = course, onClick = { actions.onCourseClick(course) })
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textStrongS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        modifier = modifier,
    )
}

@Composable
private fun EmptyResults(
    query: String,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DsText(
                text = "'$query' 검색 결과가 없어요",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel1,
            )
            Spacer(Modifier.height(6.dp))
            DsText(
                text = "다른 말로 다시 찾아보세요",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }
    }
}
