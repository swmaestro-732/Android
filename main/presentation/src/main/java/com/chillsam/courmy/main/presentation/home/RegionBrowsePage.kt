package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.RecommendedCoursesPage
import com.chillsam.courmy.main.presentation.my.RegionMapCanvas
import com.chillsam.courmy.main.presentation.my.RegionMapIntent
import com.chillsam.courmy.main.presentation.my.RegionMapUiState
import com.chillsam.courmy.main.presentation.my.RegionMapViewModel

/**
 * 지역별 둘러보기 — 전체 화면 전국 지도.
 *
 * 관심 지역 편집과 같은 지도([RegionMapCanvas])를 재사용하되, 선택이 아니라 **탐색**으로 쓴다.
 * 시·도를 누르면 그 지역으로 확대([RegionMapIntent.Browse])하고, 시·군·구를 누르면 그 지역의 코스
 * 목록([RecommendedCoursesPage.regionRoute])으로 이동한다(지도의 Toggle 을 이동으로 재활용).
 */
@Composable
fun RegionBrowsePage(
    modifier: Modifier = Modifier,
    initialProvince: String? = null,
    provinceName: String? = null,
    viewModel: RegionMapViewModel = hiltViewModel(),
) {
    val color = DesignSystemThemeImpl.designSystemColor
    StatusBarColor(color.bgDefaultLevel0)
    val navigationHelper = LocalNavigationHelper.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(RegionMapIntent.Initialize(editing = false, regions = emptyList()))
    }
    // 홈 타일 지도에서 특정 시·도로 들어오면, 지도가 로드된 뒤 그 시·도로 한 번만 확대해 둔다.
    // 이후 사용자가 "전국"으로 되돌리면 다시 확대하지 않도록 플래그로 막는다.
    var autoFocused by rememberSaveable { mutableStateOf(false) }
    val mapReady = !state.isLoading && state.regions.isNotEmpty()
    LaunchedEffect(initialProvince, mapReady) {
        if (!autoFocused && initialProvince != null && mapReady) {
            viewModel.onIntent(RegionMapIntent.Browse(initialProvince))
            autoFocused = true
        }
    }

    // 시·군·구 탭은 선택이 아니라 그 지역 코스 목록으로 이동시킨다. 나머지(시·도 확대 등)는 지도 VM 이 처리.
    val handleIntent: (RegionMapIntent) -> Unit = { intent ->
        if (intent is RegionMapIntent.Toggle) {
            state.regions.firstOrNull { it.area.code == intent.code }?.area?.let { area ->
                navigationHelper.navigateByRoute(
                    RecommendedCoursesPage.regionRoute(sigungu = area.code, regionName = area.shortName),
                )
            }
        } else {
            viewModel.onIntent(intent)
        }
    }

    // 확대한 시·도의 표시 이름(진입 인자 우선, 없으면 지도 데이터에서 복원).
    val focusedName =
        state.provinceCode?.let { code ->
            provinceName ?: state.regions
                .firstOrNull { it.area.code == code }
                ?.area
                ?.shortName
        }
    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        RegionBrowseTopBar(
            focusedName = if (state.provinceCode != null) focusedName else null,
            onBack = { navigationHelper.navigateToBack() },
            onWhole = {
                val code = state.provinceCode ?: return@RegionBrowseTopBar
                navigationHelper.navigateByRoute(
                    RecommendedCoursesPage.regionRoute(sigungu = code, regionName = focusedName ?: code),
                )
            },
            onNationwide = { viewModel.onIntent(RegionMapIntent.Browse(null)) },
        )
        DsText(
            text = if (state.provinceCode == null) "지역을 눌러 그 지역 코스를 둘러보세요" else "시·군·구를 눌러 코스를 둘러보세요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        RegionBrowseBody(
            state = state,
            onIntent = handleIntent,
            onRetry = { viewModel.onIntent(RegionMapIntent.Retry) },
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
    }
}

/** 지도 본문: 로딩 스피너·로드 실패 재시도·행정구역 지도 중 하나를 그린다. */
@Composable
private fun RegionBrowseBody(
    state: RegionMapUiState,
    onIntent: (RegionMapIntent) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(color = color.contentAccent)
            }

            state.loadError -> {
                DsText(
                    text = "지도를 불러오지 못했어요.",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                    modifier = Modifier.clickable(onClick = onRetry).padding(16.dp),
                )
            }

            else -> {
                RegionMapCanvas(state = state, onIntent = onIntent, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/** 상단 바. [focusedName] 이 있으면(시·도 확대 상태) "{지역} 전체"·"전국" 액션을 보여준다. */
@Composable
private fun RegionBrowseTopBar(
    focusedName: String?,
    onBack: () -> Unit,
    onWhole: () -> Unit,
    onNationwide: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left_24),
                contentDescription = "뒤로가기",
                tint = color.contentDefaultLevel0,
            )
        }
        DsText(
            text = "지역별 둘러보기",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        if (focusedName != null) {
            DsText(
                text = "$focusedName 전체",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentAccent,
                modifier = Modifier.clickable(onClick = onWhole).padding(horizontal = 10.dp, vertical = 8.dp),
            )
            DsText(
                text = "전국",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.clickable(onClick = onNationwide).padding(horizontal = 10.dp, vertical = 8.dp),
            )
        }
    }
}
