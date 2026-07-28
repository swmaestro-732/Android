package com.chillsam.courmy.main.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.coroutines.launch

private const val PAGE_COUNT = 3

private val ONBOARDING_HEADINGS =
    listOf(
        "저장만 해둔 장소,\n그대로 두고 계신가요?",
        "저장한 장소들이\n걷고 싶은 코스가 돼요",
        "함께 나누는\n로컬 큐레이션",
    )

/**
 * 온보딩 화면(FS-02). 3컷 인트로를 페이저로 넘기고, 마지막 컷에서 로그인/둘러보기로 시작한다.
 * [onLogin]/[onBrowse]/[onSkip] 은 각각 온보딩 완료 처리 후 다음 화면으로 이동한다.
 */
@Composable
fun OnboardingPage(
    onLogin: () -> Unit,
    onBrowse: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == PAGE_COUNT - 1

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel1)
                .statusBarsPadding(),
    ) {
        PagerDots(
            count = PAGE_COUNT,
            selected = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp),
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { page ->
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)) {
                DsText(
                    text = ONBOARDING_HEADINGS[page],
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                )
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    OnboardingIllustration(page = page)
                }
            }
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            if (isLastPage) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DsButton(text = "로그인 하기", onClick = onLogin)
                    DsButton(text = "로그인 없이 둘러보기", variant = DsButtonVariant.Secondary, onClick = onBrowse)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    DsText(
                        text = "건너뛰기",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                        modifier = Modifier.clickable(onClick = onSkip),
                    )
                    NextButton(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    )
                }
            }
        }
    }
}

/** 페이지 인디케이터 점(현재 페이지만 강조·확장). */
@Composable
private fun PagerDots(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { index ->
            val isSelected = index == selected
            Box(
                modifier =
                    Modifier
                        .height(6.dp)
                        .width(if (isSelected) 18.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) color.contentAccent else color.borderDefaultLevel1),
            )
        }
    }
}

/** "다음 →" 컴팩트 버튼. */
@Composable
private fun NextButton(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(color.bgAccent)
                .clickable(onClick = onClick)
                .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        DsText(
            text = "다음",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentOnAccent,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentOnAccent,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** 컷별 라인아트 일러스트(기존 아이콘 + 도형으로 근사). */
@Composable
private fun OnboardingIllustration(page: Int) {
    val color = DesignSystemThemeImpl.designSystemColor
    when (page) {
        0 -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel3,
                        modifier = Modifier.size(20.dp),
                    )
                    Box(
                        modifier =
                            Modifier
                                .size(width = 60.dp, height = 96.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, color.contentDefaultLevel1, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_bookmark_filled_24),
                            contentDescription = null,
                            tint = color.contentDefaultLevel1,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_location_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel3,
                        modifier = Modifier.size(16.dp),
                    )
                }
                GroundLine()
            }
        }

        1 -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.bgAccentSubtle)
                            .padding(24.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tab_map_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel1,
                        modifier = Modifier.size(72.dp),
                    )
                }
            }
        }

        else -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_heart_24),
                    contentDescription = null,
                    tint = color.contentDefaultLevel1,
                    modifier = Modifier.size(30.dp),
                )
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel1,
                        modifier = Modifier.size(24.dp),
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_location_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel1,
                        modifier = Modifier.size(24.dp),
                    )
                }
                GroundLine()
            }
        }
    }
}

@Composable
private fun GroundLine() {
    Spacer(Modifier.height(10.dp))
    Box(
        modifier =
            Modifier
                .width(140.dp)
                .height(1.dp)
                .background(DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1),
    )
}
