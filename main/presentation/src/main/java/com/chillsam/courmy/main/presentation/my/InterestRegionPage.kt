package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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

/** 드롭다운에 한 번에 보이는 최대 검색 결과 수. 이보다 많으면 스크롤(휠)로 탐색. */
private const val MAX_VISIBLE_RESULTS = 4
private val RESULT_ROW_HEIGHT = 56.dp

/** 검색 결과 한 건(동네·지하철역). 실 검색 API 연동 전 임시 데이터. */
private data class RegionResult(
    val name: String,
    val sub: String,
)

/** 임시 검색 데이터셋. 실 지역·역 검색 API 붙기 전 드롭다운 시연용. */
private val REGION_RESULTS =
    listOf(
        RegionResult("성수동", "서울 성동구"),
        RegionResult("성수역", "2호선 · 서울 성동구"),
        RegionResult("성수연방", "성수동 카페거리"),
        RegionResult("서울숲", "성수동 인근 공원"),
        RegionResult("뚝섬역", "2호선 · 성수동 인근"),
        RegionResult("성신여대입구역", "4호선 · 우이신설선"),
        RegionResult("연남동", "서울 마포구"),
        RegionResult("홍대입구역", "2호선 · 공항철도"),
        RegionResult("합정동", "서울 마포구"),
        RegionResult("망원동", "서울 마포구"),
        RegionResult("한남동", "서울 용산구"),
        RegionResult("이태원역", "6호선 · 용산구"),
        RegionResult("을지로3가역", "2호선 · 3호선"),
        RegionResult("익선동", "서울 종로구"),
        RegionResult("서촌", "서울 종로구"),
        RegionResult("삼청동", "서울 종로구"),
    )

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

            // 검색어가 있으면 입력 글자가 포함된 결과만 드롭다운으로. 최대 4개 노출, 나머지는 스크롤.
            if (query.isNotBlank()) {
                val results = REGION_RESULTS.filter { it.name.contains(query) || it.sub.contains(query) }
                SearchDropdown(
                    results = results,
                    onSelect = { name ->
                        if (name !in selected) selected = selected + name
                        query = ""
                    },
                )
            }

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

/**
 * 검색 결과 드롭다운(Figma FS-07-Search). 한 번에 [MAX_VISIBLE_RESULTS]개까지 보이고
 * 그보다 많으면 내부 스크롤(휠)로 탐색한다. 결과가 없으면 안내 문구를 보인다.
 */
@Composable
private fun SearchDropdown(
    results: List<RegionResult>,
    onSelect: (String) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(shape)
                .border(1.dp, color.borderDefaultLevel1, shape)
                .background(color.bgDefaultLevel1),
    ) {
        if (results.isEmpty()) {
            DsText(
                text = "검색 결과가 없어요",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            )
        } else {
            Column(
                // 4개 높이까지만 보이고 나머지는 스크롤(휠)로.
                modifier =
                    Modifier
                        .heightIn(max = RESULT_ROW_HEIGHT * MAX_VISIBLE_RESULTS)
                        .verticalScroll(rememberScrollState()),
            ) {
                results.forEachIndexed { index, result ->
                    ResultRow(result = result, onClick = { onSelect(result.name) })
                    if (index < results.lastIndex) {
                        HorizontalDivider(color = color.borderDefaultLevel0)
                    }
                }
            }
        }
    }
}

/** 드롭다운 결과 한 줄: 위치 아이콘 + 이름(강조) + 상세(지역·노선). */
@Composable
private fun ResultRow(
    result: RegionResult,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(RESULT_ROW_HEIGHT)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_24),
            contentDescription = null,
            tint = color.contentDefaultLevel3,
            modifier = Modifier.size(18.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = result.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel1,
            )
            DsText(
                text = result.sub,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }
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
