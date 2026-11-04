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
    viewModel: RegionMapViewModel = hiltViewModel(),
) {
    val color = DesignSystemThemeImpl.designSystemColor
    StatusBarColor(color.bgDefaultLevel0)
    val navigationHelper = LocalNavigationHelper.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(RegionMapIntent.Initialize(editing = false, regions = emptyList()))
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

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navigationHelper.navigateToBack() }) {
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
            // 시·도로 확대한 상태면 전국 지도로 되돌릴 수 있게 한다.
            if (state.provinceCode != null) {
                DsText(
                    text = "전국",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentAccent,
                    modifier =
                        Modifier
                            .clickable { viewModel.onIntent(RegionMapIntent.Browse(null)) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                )
            }
        }
        DsText(
            text = if (state.provinceCode == null) "지역을 눌러 그 지역 코스를 둘러보세요" else "시·군·구를 눌러 코스를 둘러보세요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = color.contentAccent)
                }

                state.loadError -> {
                    DsText(
                        text = "지도를 불러오지 못했어요.",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                        modifier = Modifier.clickable { viewModel.onIntent(RegionMapIntent.Retry) }.padding(16.dp),
                    )
                }

                else -> {
                    RegionMapCanvas(state = state, onIntent = handleIntent, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
