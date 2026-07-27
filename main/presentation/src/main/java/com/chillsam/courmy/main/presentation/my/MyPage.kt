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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.settings.SettingsPage
import com.chillsam.courmy.main.entity.my.MyCourseVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 마이·프로필 화면(FS-15). [MyViewModel] 이 로드한 프로필 상태에 따라
 * 정상([MyContent]) / 로딩([MyLoading]) / 에러([MyError]) 를 분기한다.
 */
@Composable
fun MyPage(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    when {
        profile != null -> {
            MyContent(profile = profile, modifier = modifier)
        }

        uiState.isLoading -> {
            MyLoading(modifier = modifier)
        }

        else -> {
            MyError(
                message = uiState.errorMessage,
                onRetry = { viewModel.onIntent(MyProfileIntent.Retry) },
                modifier = modifier,
            )
        }
    }
}

/** 프로필 로드 성공 시 실제 마이 화면(커버+아바타 헤더, 통계, "내 코스" 그리드, 하단 탭바). */
@Composable
private fun MyContent(
    profile: MyProfileVO,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
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
            Spacer(Modifier.height(60.dp)) // 커버에 걸친 아바타 아래 절반만큼 여백
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
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
            StatsRow(profile = profile)
            MyCoursesSection(profile = profile)
            Spacer(Modifier.height(20.dp))
        }

        CourmyBottomBar(
            selectedTab = MainTab.MY,
            onTabSelected = { tab ->
                if (tab == MainTab.HOME) navigationHelper.navigateTo(HomePage)
            },
        )
    }
}

/** 프로필 로딩 중 스피너. */
@Composable
private fun MyLoading(modifier: Modifier = Modifier) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = color.contentAccent)
    }
}

/** 프로필 로드 실패 시 안내 + 재시도. */
@Composable
private fun MyError(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = message ?: "프로필을 불러오지 못했습니다.",
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = color.contentDefaultLevel1,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        DsButton(text = "다시 시도", onClick = onRetry)
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
                    .background(
                        // 세로 그라디언트: 상단 bgAccent(Forest600) → 하단 bgAccentPressed(Forest700).
                        Brush.verticalGradient(
                            listOf(color.bgAccent, color.bgAccentPressed),
                        ),
                    ).clipToBounds(),
        ) {
            // 데코 원(커버 밖으로 넘치는 부분은 clip). Figma 원본은 좌하단 민트·우상단 흰색이며,
            // 팔레트에 없는 민트 원색 대신 저투명도 흰색 오버레이로 근사한다.
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-36).dp, y = 40.dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(color.contentOnAccent.copy(alpha = 0.08f)),
            )
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-56).dp, y = 24.dp)
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(color.contentOnAccent.copy(alpha = 0.10f)),
            )
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
        // 흰색 링 + 소프트 드롭 섀도우로 커버·배경 위에서 떠 보이게 한다(Figma FS-15).
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 48.dp)
                    .size(96.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = color.contentDefaultLevel0.copy(alpha = 0.5f),
                        spotColor = color.contentDefaultLevel0.copy(alpha = 0.5f),
                    ).clip(CircleShape)
                    .background(color.bgDefaultLevel1)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(color.imagePlaceholder),
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

/** 내 코스 · 팔로워 · 팔로잉 3열 통계(항목 사이 세로 구분선). */
@Composable
private fun StatsRow(profile: MyProfileVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(value = profile.myCourseCount.toString(), label = "내 코스")
        VerticalDivider(
            thickness = 1.dp,
            color = color.borderDefaultLevel1,
            modifier = Modifier.height(28.dp),
        )
        StatItem(value = profile.followerCount, label = "팔로워")
        VerticalDivider(
            thickness = 1.dp,
            color = color.borderDefaultLevel1,
            modifier = Modifier.height(28.dp),
        )
        StatItem(value = profile.followingCount, label = "팔로잉")
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

/** 내 코스 2열 카드 그리드. */
@Composable
private fun MyCoursesSection(profile: MyProfileVO) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
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
    course: MyCourseVO,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .shadow(
                    // 낮은 elevation은 blur 가 거의 없어 테두리 선처럼 보인다.
                    // elevation 을 올리고 spot/ambient 를 옅게 줘 부드럽게 퍼지게 한다(API 28+ 반영).
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = color.contentDefaultLevel0.copy(alpha = 0.12f),
                    spotColor = color.contentDefaultLevel0.copy(alpha = 0.16f),
                ).clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1),
    ) {
        // 썸네일: 카드 상단. 따라감 배지는 썸네일 하단 좌측(반투명 진회색).
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .background(color.imagePlaceholder),
        ) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.contentDefaultLevel2)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                DsText(
                    text = course.tracingLabel,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DsText(
                text = course.spotLabel,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
