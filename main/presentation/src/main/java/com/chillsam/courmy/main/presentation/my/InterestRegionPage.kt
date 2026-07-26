package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.component.BackTopBar

private val POPULAR_REGIONS = listOf("홍대", "잠실", "을지로", "이태원", "망원", "삼청", "서촌", "압구정")

/** 관심 지역 편집 화면(FS-07). 지역을 검색·선택하고 선택 칩을 삭제한다. */
@Composable
fun InterestRegionPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(listOf("성수", "연남", "한남")) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "관심 지역", onBack = { navigationHelper.navigateToBack() })

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            DsTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "동네·지하철역 검색",
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            SectionLabel(text = "선택한 지역 ${selected.size}")
            SelectedRegionChips(
                regions = selected,
                onRemove = { region -> selected = selected - region },
            )

            SectionLabel(text = "인기 지역")
            PopularRegionChips(
                regions = POPULAR_REGIONS,
                selected = selected.toSet(),
                onToggle = { region ->
                    selected = if (region in selected) selected - region else selected + region
                },
            )
        }

        DsButton(
            text = "저장",
            onClick = { navigationHelper.navigateToBack() },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textStrongM,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectedRegionChips(
    regions: List<String>,
    onRemove: (String) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        regions.forEach { region ->
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgAccent)
                        .clickable { onRemove(region) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DsText(
                    text = region,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentOnAccent,
                )
                DsText(
                    text = "×",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentOnAccent,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PopularRegionChips(
    regions: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        regions.forEach { region ->
            DsChip(
                text = region,
                selected = region in selected,
                onClick = { onToggle(region) },
            )
        }
    }
}
