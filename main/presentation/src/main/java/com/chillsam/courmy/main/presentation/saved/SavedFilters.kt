package com.chillsam.courmy.main.presentation.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@Composable
internal fun SavedHeader(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.bgDefaultLevel1)
                .padding(3.dp)
                .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        listOf(SAVED_TAB_COURSES to "코스", SAVED_TAB_PLACES to "장소").forEach { (tab, title) ->
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .then(if (selectedTab == tab) Modifier.background(color.bgDefaultLevel0) else Modifier)
                        .selectable(selected = selectedTab == tab, role = Role.Tab, onClick = { onSelectTab(tab) }),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = title,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = if (selectedTab == tab) color.contentDefaultLevel0 else color.contentDefaultLevel3,
                )
            }
        }
    }
}

@Composable
internal fun SavedPlaceFilters(
    state: SavedPlacesUIState,
    onIntent: (SavedPlacesIntent) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val categories =
        listOf(null) +
            (state.places.map { it.categoryLabel } + listOfNotNull(state.selectedCategory))
                .filter { it.isNotBlank() }
                .distinct()
    Row(
        modifier = Modifier.heightIn(min = 48.dp).horizontalScroll(rememberScrollState()).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        categories.forEach { category ->
            val count = state.places.count { category == null || it.categoryLabel == category }
            SavedFilterChip(
                label = "${category ?: "전체"} $count",
                selected = state.selectedCategory == category,
                onClick = { onIntent(SavedPlacesIntent.SelectCategory(category)) },
            )
        }
    }
    if (state.hasNext) {
        DsText(
            text = "불러온 장소 기준",
            style = DesignSystemThemeImpl.typeScale.textExtraXS,
            color = color.contentDefaultLevel3,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
    SavedStatusTabs(
        selectedSecond = state.showVisited,
        firstLabel = "미방문 ${state.categoryPlaces.count { !it.visited }}",
        secondLabel = "방문 ${state.categoryPlaces.count { it.visited }}",
        onSelect = { onIntent(SavedPlacesIntent.SelectVisited(it)) },
    )
}

/** 장소 탭의 작은 필터 크기를 폴더와 카테고리에 함께 사용한다. */
@Composable
internal fun SavedFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(50)
    Box(
        modifier =
            Modifier
                .padding(vertical = 4.dp)
                .clip(shape)
                .background(if (selected) color.contentDefaultLevel0 else color.bgDefaultLevel1)
                .border(1.dp, if (selected) color.contentDefaultLevel0 else color.borderDefaultLevel0, shape)
                .selectable(selected = selected, role = Role.Tab, onClick = onClick)
                .padding(horizontal = 15.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = if (selected) color.contentOnAccent else color.contentDefaultLevel1,
        )
    }
}

@Composable
internal fun SavedStatusTabs(
    selectedSecond: Boolean,
    firstLabel: String,
    secondLabel: String,
    onSelect: (Boolean) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.padding(top = 12.dp).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        listOf(false to firstLabel, true to secondLabel).forEach { (second, label) ->
            val selected = selectedSecond == second
            Column(
                modifier = Modifier.selectable(selected = selected, role = Role.Tab, onClick = { onSelect(second) }),
            ) {
                DsText(
                    text = label,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = if (selected) color.contentDefaultLevel1 else color.contentDefaultLevel3,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    color = if (selected) color.contentDefaultLevel1 else color.bgDefaultLevel0,
                    modifier = Modifier.width(70.dp),
                )
            }
        }
    }
    HorizontalDivider(color = color.borderDefaultLevel0, modifier = Modifier.padding(bottom = 16.dp))
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390)
@Composable
private fun SavedFiltersPreview() {
    com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme {
        Column(Modifier.padding(20.dp)) {
            SavedHeader(selectedTab = SAVED_TAB_PLACES, onSelectTab = {})
            SavedPlaceFilters(state = SavedPlacesUIState(isLoading = false), onIntent = {})
        }
    }
}
