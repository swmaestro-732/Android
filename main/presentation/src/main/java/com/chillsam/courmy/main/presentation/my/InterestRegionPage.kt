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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.component.SignupProgressBar

private val POPULAR_REGIONS = listOf("홍대", "잠실", "을지로", "이태원", "망원", "삼청", "서촌", "압구정")
private const val MIN_REGION_COUNT = 3

/** 관심 지역 편집 화면(FS-07). 지역을 검색·선택하고 선택 칩을 삭제한다. */
@Composable
fun InterestRegionPage(
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    progressStep: Int? = null,
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(listOf("성수", "연남", "한남")) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        // 상단 바 영역은 흰색(Figma FS-07). 가운데 콘텐츠만 Gray200.
        if (progressStep != null) {
            SignupProgressBar(
                step = progressStep,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color.bgDefaultLevel1)
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
                BackTopBar(title = "관심 지역", onBack = { navigationHelper.navigateToBack() })
            }
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            DsText(
                text = "자주 가는\n동네는요?",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
            DsText(
                text = "여러 곳을 골라도 좋아요.",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            DsTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "동네·지하철역 검색",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel3,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )

            SectionLabel(text = "선택한 지역", count = selected.size)
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
            // 회원가입 플로우면 "다음", 편집이면 "저장".
            text = if (onNext != null) "다음" else "저장",
            enabled = selected.size >= MIN_REGION_COUNT,
            onClick = { onNext?.invoke() ?: navigationHelper.navigateToBack() },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

/** 섹션 라벨(작은 회색). [count] 가 있으면 뒤에 강조색으로 개수 표시(Figma FS-07). */
@Composable
private fun SectionLabel(
    text: String,
    count: Int? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)) {
        DsText(
            text = if (count != null) "$text " else text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
        if (count != null) {
            DsText(
                text = "$count",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
    }
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
                        .padding(start = 15.dp, end = 12.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DsText(
                    text = region,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentOnAccent,
                )
                Icon(
                    painter = painterResource(R.drawable.close_small_24),
                    contentDescription = "삭제",
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(14.dp),
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
