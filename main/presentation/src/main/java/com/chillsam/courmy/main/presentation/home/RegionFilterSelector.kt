package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.R
import com.chillsam.courmy.common.presentation.R as CommonR

/** 선택 후 접히는 지역 선택기. 지역이 많으면 펼친 영역 안에서 세로로 스크롤한다. */
@Composable
internal fun RegionFilterSelector(
    subRegions: List<SubRegion>,
    selected: String?,
    onSelect: (String?) -> Unit,
    isLoading: Boolean,
    loadError: Boolean,
    onRetry: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val color = DesignSystemThemeImpl.designSystemColor
    val selectedRegion = subRegions.firstOrNull { it.code == selected }
    val allAreas = stringResource(R.string.main_region_filter_all)
    val selectedLabel = selected?.let { districtLabel(it, selectedRegion?.name ?: it) } ?: allAreas
    val expansionState =
        stringResource(
            if (expanded) R.string.main_region_filter_expanded else R.string.main_region_filter_collapsed,
        )
    Column(
        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding).padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.bgDefaultLevel0)
                    .clickable(role = Role.Button) { expanded = !expanded }
                    .semantics { stateDescription = expansionState }
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                DsText(
                    text = stringResource(R.string.main_region_filter_label),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
                DsText(
                    text = selectedLabel,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
            }
            Icon(
                painter = painterResource(CommonR.drawable.ic_chevron_down_24),
                contentDescription = null,
                tint = color.contentDefaultLevel1,
                modifier = Modifier.rotate(if (expanded) 180f else 0f),
            )
        }
        if (expanded) {
            when {
                isLoading -> {
                    DsText(text = stringResource(R.string.main_region_filter_loading))
                }

                loadError -> {
                    DsText(text = stringResource(R.string.main_region_filter_error))
                    TextButton(onClick = onRetry) {
                        DsText(text = stringResource(R.string.main_recommended_retry))
                    }
                }

                subRegions.isEmpty() -> {
                    DsText(text = stringResource(R.string.main_region_filter_empty))
                }
            }
            FlowRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsChip(
                    text = allAreas,
                    selected = selected == null,
                    modifier = Modifier.semantics { this.selected = selected == null },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    },
                )
                subRegions.forEach { region ->
                    DsChip(
                        text = districtLabel(region.code, region.name),
                        selected = selected == region.code,
                        modifier = Modifier.semantics { this.selected = selected == region.code },
                        onClick = {
                            onSelect(region.code)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun RegionFilterSelectorPreview() {
    com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme {
        RegionFilterSelector(
            subRegions = listOf(SubRegion("11110", "종로구"), SubRegion("11140", "중구")),
            selected = "11110",
            onSelect = {},
            isLoading = false,
            loadError = false,
            onRetry = {},
        )
    }
}
