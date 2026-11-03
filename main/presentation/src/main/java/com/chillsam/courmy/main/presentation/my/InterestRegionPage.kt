package com.chillsam.courmy.main.presentation.my

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.StepProgressBar
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.login.SignupSelectionStore

/** 온보딩·프로필 편집 공용. 시·도 전체 또는 시·군·구를 지도에서 고른다. */
@Composable
fun InterestRegionPage(
    modifier: Modifier = Modifier,
    onNext: ((List<AreaVO>) -> Unit)? = null,
    progressStep: Int? = null,
) {
    val viewModel: RegionMapViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigation = LocalNavigationHelper.current
    val editing = onNext == null
    LaunchedEffect(Unit) {
        viewModel.onIntent(
            RegionMapIntent.Initialize(editing, if (editing) emptyList() else SignupSelectionStore.regions),
        )
    }
    LaunchedEffect(state.selected, state.isLoading, state.loadError) {
        if (!editing && !state.isLoading && !state.loadError) SignupSelectionStore.regions = state.selected
    }
    LaunchedEffect(state.saved) { if (state.saved) navigation.navigateToBack() }
    BackHandler(enabled = state.provinceCode != null) { viewModel.onIntent(RegionMapIntent.Browse(null)) }
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    InterestRegionScreen(
        state = state,
        progressStep = progressStep,
        onIntent = viewModel::onIntent,
        onBack = {
            if (state.provinceCode != null) {
                viewModel.onIntent(RegionMapIntent.Browse(null))
            } else {
                navigation.navigateToBack()
            }
        },
        onContinue = { onNext?.invoke(state.selected) ?: viewModel.onIntent(RegionMapIntent.Save) },
        modifier = modifier,
    )
}

@Composable
internal fun InterestRegionScreen(
    state: RegionMapUiState,
    progressStep: Int?,
    onIntent: (RegionMapIntent) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val type = DesignSystemThemeImpl.typeScale
    Column(modifier.fillMaxSize().background(color.bgDefaultLevel1)) {
        if (progressStep != null) {
            StepProgressBar(
                step = progressStep,
                modifier = Modifier.statusBarsPadding().padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            )
        } else {
            BackTopBar(title = "관심 지역", onBack = onBack)
        }
        Column(Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp)) {
            DsText("자주 가는 지역을 골라주세요", style = type.textStrongM, color = color.contentDefaultLevel0)
            DsText(
                "최소 1개 · 시·도 전체 또는 시·군·구 선택",
                style = type.textRegularXS,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        when {
            state.isLoading -> {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = color.contentAccent)
                }
            }

            state.loadError -> {
                Column(
                    Modifier.weight(1f).padding(ScreenHorizontalPadding),
                    verticalArrangement = Arrangement.Center,
                ) {
                    DsText("지역을 불러오지 못했어요.", style = type.textRegularS)
                    DsButton(
                        "다시 시도",
                        onClick = { onIntent(RegionMapIntent.Retry) },
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }

            else -> {
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    RegionMapCanvas(state, onIntent)
                    RegionMapInsets(state, onIntent)
                    if (state.provinceCode != null) {
                        DsChip(
                            text = "‹ 전국 보기",
                            selected = false,
                            onClick = { onIntent(RegionMapIntent.Browse(null)) },
                            modifier =
                                Modifier
                                    .align(
                                        Alignment.TopStart,
                                    ).padding(12.dp)
                                    .minimumInteractiveComponentSize(),
                        )
                    }
                }
                RegionChoices(state, onIntent)
            }
        }
        RegionSelectionFooter(state, progressStep, onIntent, onContinue)
    }
}

@Composable
private fun RegionSelectionFooter(
    state: RegionMapUiState,
    progressStep: Int?,
    onIntent: (RegionMapIntent) -> Unit,
    onContinue: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val type = DesignSystemThemeImpl.typeScale
    Column(
        Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (state.selected.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                item { DsText("선택 ${state.selected.size} ·", style = type.textRegularXS) }
                items(state.selected, key = { it.code }) { area ->
                    val label = if (area.level == AreaLevel.SIDO) "${area.shortName} 전체" else area.fullName
                    DsText(
                        text = "$label ×",
                        style = type.textRegularXS,
                        color = color.contentDefaultLevel1,
                        modifier =
                            Modifier
                                .minimumInteractiveComponentSize()
                                .clickable(enabled = !state.isSaving) { onIntent(RegionMapIntent.Remove(area.code)) }
                                .semantics { contentDescription = "$label 선택 해제" },
                    )
                }
            }
        }
        if (state.saveError) {
            DsText(
                "저장하지 못했어요. 선택한 지역을 유지했으니 다시 시도해 주세요.",
                style = type.textRegularXS,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
            )
        }
        DsButton(
            text =
                when {
                    state.selected.isEmpty() -> "지역을 1개 이상 선택해 주세요"
                    progressStep != null -> "다음"
                    else -> "저장"
                },
            enabled = state.canContinue,
            loading = state.isSaving,
            onClick = onContinue,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
    }
}

@Composable
private fun RegionChoices(
    state: RegionMapUiState,
    onIntent: (RegionMapIntent) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val gridState = rememberLazyGridState()
    LaunchedEffect(state.provinceCode) { gridState.scrollToItem(0) }
    LaunchedEffect(state.selectionFocusCode, state.provinceCode) {
        val index = state.visibleRegions.indexOfFirst { it.area.code == state.selectionFocusCode }
        val visible = gridState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
        val fullyVisible =
            visible?.let {
                it.offset.y >= gridState.layoutInfo.viewportStartOffset &&
                    it.offset.y + it.size.height <= gridState.layoutInfo.viewportEndOffset
            } == true
        if (index >= 0 && !fullyVisible) gridState.animateScrollToItem(index)
    }
    val province = state.province
    Column(
        Modifier
            .fillMaxWidth()
            .background(color.bgDefaultLevel0)
            .padding(horizontal = ScreenHorizontalPadding, vertical = 10.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(8.dp)),
        ) {
            if (province != null) {
                RegionTableCell(
                    label = "${province.area.shortName} 전체",
                    description = "${province.area.fullName} 전체 선택, 하위 지역 모두 포함",
                    selected = state.selected.any { it.code == province.area.code },
                    enabled = !state.isSaving,
                    onClick = { onIntent(RegionMapIntent.Toggle(province.area.code)) },
                )
            }
            if (state.visibleRegions.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 168.dp),
                ) {
                    items(state.visibleRegions, key = { it.area.code }) { region ->
                        RegionTableCell(
                            label = region.area.shortName,
                            description = if (province == null) "${region.area.fullName} 확대" else region.area.fullName,
                            selected = state.selected.any { region.area.code.startsWith(it.code) },
                            enabled = !state.isSaving,
                            onClick = {
                                onIntent(
                                    if (province == null) {
                                        RegionMapIntent.Browse(region.area.code)
                                    } else {
                                        RegionMapIntent.Toggle(region.area.code)
                                    },
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegionTableCell(
    label: String,
    description: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(if (selected) color.bgAccentSubtle else color.bgDefaultLevel1)
                .drawBehind {
                    val stroke = 1.dp.toPx()
                    drawLine(
                        color.borderDefaultLevel0,
                        Offset(0f, size.height),
                        Offset(size.width, size.height),
                        stroke,
                    )
                    drawLine(color.borderDefaultLevel0, Offset(size.width, 0f), Offset(size.width, size.height), stroke)
                }.selectable(selected = selected, enabled = enabled, role = Role.Checkbox, onClick = onClick)
                .semantics { contentDescription = description }
                .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = if (selected) "✓ $label" else label,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = if (selected) color.contentAccent else color.contentDefaultLevel1,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
