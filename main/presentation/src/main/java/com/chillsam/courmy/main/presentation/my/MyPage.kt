package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.settings.SettingsPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 마이·프로필 화면(FS-15). 커버+아바타 헤더, 닉네임·소개, 통계(저장·내 코스·팔로워),
 * "내 코스" 카드 그리드, 하단 탭바로 구성한다. 표시 전용이며 [MyViewModel] 의 더미 프로필을 렌더한다.
 */
@Composable
fun MyPage(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val color = DesignSystemThemeImpl.designSystemColor

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileHeader(onSettings = { navigationHelper.navigateTo(SettingsPage) })
            Spacer(Modifier.height(52.dp)) // 커버에 걸친 아바타 아래 절반만큼 여백
            DsText(
                text = profile.nickname,
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
            )
            Spacer(Modifier.height(4.dp))
            DsText(
                text = "@${profile.handle}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
            DsText(
                text = profile.bio,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel1,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 12.dp),
            )
            StatsRow(profile = profile)
            HorizontalDivider(
                thickness = 1.dp,
                color = color.borderDefaultLevel0,
                modifier = Modifier.padding(top = 20.dp),
            )
            MyCoursesSection(profile = profile)
            Spacer(Modifier.height(16.dp))
        }

        CourmyBottomBar(
            selectedTab = MainTab.MY,
            onTabSelected = { tab ->
                if (tab == MainTab.HOME) navigationHelper.navigateTo(HomePage)
            },
        )
    }
}

/** 초록 커버 + 우상단 공유/설정 버튼 + 커버 하단에 걸친 원형 아바타. */
@Composable
private fun ProfileHeader(onSettings: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(color.bgAccent),
        ) {
            Row(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CoverIconButton(symbol = "↗", onClick = {})
                CoverIconButton(symbol = "⚙", onClick = onSettings)
            }
        }
        // 커버 하단에 걸친 아바타(하단 절반이 아래로 넘침).
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 48.dp)
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(color.bgDefaultLevel0)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(color.borderDefaultLevel0),
        )
    }
}

@Composable
private fun CoverIconButton(
    symbol: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.bgDefaultLevel0)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = symbol,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel0,
        )
    }
}

/** 저장 · 내 코스 · 팔로워 3열 통계. */
@Composable
private fun StatsRow(profile: MyProfileUiState) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(value = profile.savedCount, label = "저장")
        StatItem(value = profile.myCourseCount.toString(), label = "내 코스")
        StatItem(value = profile.followerCount, label = "팔로워")
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DsText(
            text = value,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        Spacer(Modifier.height(2.dp))
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
    }
}

/** "내 코스 N" 헤더 + 2열 카드 그리드. */
@Composable
private fun MyCoursesSection(profile: MyProfileUiState) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DsText(
                text = "내 코스",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = profile.myCourseCount.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
        }
        profile.myCourses.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { course ->
                    MyCourseCard(course = course, modifier = Modifier.weight(1f))
                }
                // 홀수 개일 때 마지막 칸 균형 맞춤.
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MyCourseCard(
    course: MyCourseCardUi,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.35f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.borderDefaultLevel0),
        ) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.contentDefaultLevel0)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                DsText(
                    text = course.tracingLabel,
                    style = DesignSystemThemeImpl.typeScale.textExtraXS,
                    color = color.contentOnAccent,
                )
            }
        }
        DsText(
            text = course.title,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
            maxLines = 1,
            modifier = Modifier.padding(top = 8.dp),
        )
        DsText(
            text = course.spotLabel,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
