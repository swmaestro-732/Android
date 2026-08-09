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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.component.StepProgressBar
import com.chillsam.courmy.common.presentation.component.alignTextFieldBorder
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.presentation.component.BackTopBar

/**
 * 인기 지역. 누르면 바로 선택된다.
 *
 * 선택된 지역은 회원가입 요청에 실을 법정동코드([AreaVO.code])를 들고 있어야 하는데 이 목록엔 이름뿐이라,
 * 탭하면 이름으로 한 번 검색해 코드가 붙은 실제 행정구역으로 바꿔 담는다(AreaSearchViewModel 참고).
 * 그래서 검색에 걸리도록 법정동 이름(~동)으로 적는다.
 */
private val POPULAR_REGIONS =
    listOf(
        "성수동",
        "연남동",
        "합정동",
        "망원동",
        "한남동",
        "이태원동",
        "익선동",
        "삼청동",
        "서교동",
        "잠실동",
        "압구정동",
        "청담동",
    )
private const val MIN_REGION_COUNT = 3

/** 드롭다운에 한 번에 보이는 최대 검색 결과 수. 이보다 많으면 스크롤(휠)로 탐색. */
private const val MAX_VISIBLE_RESULTS = 4
private val RESULT_ROW_HEIGHT = 56.dp

/** 관심 지역 편집 화면(FS-07). 지역을 검색·선택하고 선택 칩을 삭제한다. */
@Composable
fun InterestRegionPage(
    modifier: Modifier = Modifier,
    onNext: ((List<AreaVO>) -> Unit)? = null,
    progressStep: Int? = null,
) {
    // 상단이 흰색이라 상태바도 같은 색으로 이어 붙인다. 회색으로 두면 띠만 분리돼 보인다.
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val searchViewModel: AreaSearchViewModel = hiltViewModel()
    val searchState by searchViewModel.uiState.collectAsStateWithLifecycle()
    // 편집 모드에서만 저장된 관심사를 읽고 되쓴다. 회원가입은 완료 화면까지 홀더로 나른다.
    val editing = onNext == null
    val editViewModel: InterestEditViewModel = hiltViewModel()
    val editState by editViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(editing) { if (editing) editViewModel.onIntent(InterestEditIntent.Load) }
    // 회원가입은 빈 선택으로 시작하고, 편집은 불러온 값이 도착하면 그 값으로 다시 채운다.
    var selected by remember(editState.loaded) { mutableStateOf(editState.regions) }
    LaunchedEffect(editState.saved) { if (editState.saved) navigationHelper.navigateToBack() }
    val query = searchState.keyword
    val search = { keyword: String -> searchViewModel.onIntent(AreaSearchIntent.QueryChanged(keyword)) }
    // 인기 지역 탭이 실제 행정구역으로 풀리면 그대로 선택에 담는다.
    LaunchedEffect(searchState.resolved) {
        searchState.resolved?.let { area ->
            if (selected.none { it.sameAs(area) }) selected = selected + area
            searchViewModel.onIntent(AreaSearchIntent.ConsumeResolved)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        // 상단 바 영역은 흰색(Figma FS-07). 가운데 콘텐츠만 Gray200.
        if (progressStep != null) {
            StepProgressBar(
                step = progressStep,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color.bgDefaultLevel1)
                        .statusBarsPadding()
                        .padding(
                            start = ScreenHorizontalPadding,
                            end = ScreenHorizontalPadding,
                            top = 12.dp,
                            bottom = 4.dp,
                        ),
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
                    .padding(horizontal = ScreenHorizontalPadding),
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
                onValueChange = search,
                placeholder = "동네·지역 검색",
                modifier = Modifier.fillMaxWidth().alignTextFieldBorder(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel3,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )

            // 검색어가 있으면 서버 검색 결과를 드롭다운으로. 최대 4개 노출, 나머지는 스크롤.
            if (query.isNotBlank()) {
                SearchDropdown(
                    results = searchState.results,
                    isSearching = searchState.isSearching,
                    usedFallback = searchState.usedFallback,
                    onSelect = { area ->
                        // 같은 코드를 두 번 담지 않는다. 폴백 결과는 코드가 비어 이름으로 가른다.
                        if (selected.none { it.sameAs(area) }) selected = selected + area
                        search("")
                    },
                )
            }

            SectionLabel(text = "선택한 지역", count = selected.size)
            SelectedRegionChips(
                regions = selected,
                onRemove = { area -> selected = selected.filterNot { it.sameAs(area) } },
            )

            SectionLabel(text = "인기 지역")
            PopularRegionChips(
                regions = POPULAR_REGIONS,
                selectedNames = selected,
                onToggle = { name ->
                    val already = selected.filter { it.matchesName(name) }
                    // 이미 고른 지역이면 그 자리에서 뺀다. 아니면 검색을 거쳐 실제 행정구역으로 담는다.
                    if (already.isNotEmpty()) {
                        selected = selected - already.toSet()
                    } else {
                        searchViewModel.onIntent(AreaSearchIntent.ResolvePopular(name))
                    }
                },
            )
        }

        DsButton(
            // 회원가입 플로우면 "다음", 편집이면 "저장".
            text = if (onNext != null) "다음" else "저장",
            enabled = selected.size >= MIN_REGION_COUNT,
            onClick = {
                // 편집은 기기에 저장한 뒤 저장 완료 신호를 받아 닫는다(위 LaunchedEffect).
                onNext?.invoke(selected) ?: editViewModel.onIntent(InterestEditIntent.SaveRegions(selected))
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        )
    }
}

/**
 * 검색 결과 드롭다운(Figma FS-07-Search). 한 번에 [MAX_VISIBLE_RESULTS]개까지 보이고
 * 그보다 많으면 내부 스크롤(휠)로 탐색한다. 결과가 없으면 안내 문구를 보인다.
 */
@Composable
private fun SearchDropdown(
    results: List<AreaVO>,
    isSearching: Boolean,
    usedFallback: Boolean,
    onSelect: (AreaVO) -> Unit,
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
                // 검색 중에 "결과 없음"이 잠깐 스쳐 보이지 않게 상태를 갈라 보여 준다.
                text = if (isSearching) "검색 중이에요" else "검색 결과가 없어요",
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
                results.forEachIndexed { index, area ->
                    ResultRow(area = area, onClick = { onSelect(area) })
                    if (index < results.lastIndex) {
                        HorizontalDivider(color = color.borderDefaultLevel0)
                    }
                }
            }
            if (usedFallback) {
                // 서버 검색이 안 될 때 내장 목록으로 채운 상태임을 숨기지 않는다.
                DsText(
                    text = "지금은 검색 서버에 연결할 수 없어 자주 쓰는 지역만 보여 주고 있어요",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}

/** 드롭다운 결과 한 줄: 위치 아이콘 + 짧은 이름(강조) + 전체 주소. */
@Composable
private fun ResultRow(
    area: AreaVO,
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
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = area.shortName,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel1,
            )
            DsText(
                text = area.fullName,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                overflow = TextOverflow.Ellipsis,
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
    regions: List<AreaVO>,
    onRemove: (AreaVO) -> Unit,
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
                    text = region.shortName,
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

/** 인기 지역 칩. 이미 고른 지역이면 선택 상태로 그린다([POPULAR_REGIONS] 주석 참고). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PopularRegionChips(
    regions: List<String>,
    selectedNames: List<AreaVO>,
    onToggle: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        regions.forEach { region ->
            DsChip(
                text = region,
                selected = selectedNames.any { it.matchesName(region) },
                onClick = { onToggle(region) },
            )
        }
    }
}

/**
 * 인기 지역 칩 이름과 같은 곳인지.
 *
 * 검색이 칩 이름을 더 잘게 쪼갠 결과를 주므로("성수동" → "성수동1가") 정확히 같은지로는 못 가른다.
 * 그래서 이름이 포함되는지로 느슨하게 본다.
 */
private fun AreaVO.matchesName(name: String): Boolean = shortName.contains(name) || fullName.contains(name)

/**
 * 같은 지역인지 판정.
 *
 * 보통은 법정동코드로 가르지만, 검색 폴백 결과는 코드가 비어 있어 코드만 보면 서로 다른 지역이
 * 모두 같은 것으로 뭉개진다. 그래서 코드가 있을 때만 코드로, 없으면 이름으로 비교한다.
 */
private fun AreaVO.sameAs(other: AreaVO): Boolean =
    if (code.isNotBlank() && other.code.isNotBlank()) code == other.code else fullName == other.fullName
