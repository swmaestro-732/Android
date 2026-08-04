package com.chillsam.courmy.main.presentation.my

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.FollowListPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.domain.settings.SettingsPage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.profile.ProfileCoursesGrid
import com.chillsam.courmy.main.presentation.profile.ProfileCoverHeader
import com.chillsam.courmy.main.presentation.profile.ProfileCoverIconButton
import com.chillsam.courmy.main.presentation.profile.ProfileError
import com.chillsam.courmy.main.presentation.profile.ProfileLoading
import com.chillsam.courmy.main.presentation.profile.ProfileStatsRow

/**
 * 마이·프로필 화면(FS-15). [MyViewModel] 이 로드한 프로필 상태에 따라
 * 정상([MyContent]) / 로딩 / 에러 를 분기한다. 커버·통계·코스 그리드는 타유저 프로필과
 * `presentation/profile` 의 공용 컴포저블을 공유한다.
 */
@Composable
fun MyPage(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    // 프로필 편집에서 돌아왔을 때 수정된 값이 보이도록, 다시 보일 때마다 새로 불러온다.
    // 첫 resume 은 건너뛴다 — ViewModel 이 init 에서 이미 불러와 같은 요청이 두 번 나간다.
    var skipFirstResume by remember { mutableStateOf(true) }
    LifecycleResumeEffect(Unit) {
        if (skipFirstResume) {
            skipFirstResume = false
        } else {
            viewModel.onIntent(MyProfileIntent.Retry)
        }
        onPauseOrDispose {}
    }
    when {
        profile != null -> {
            MyContent(profile = profile, modifier = modifier)
        }

        uiState.isLoading -> {
            ProfileLoading(modifier = modifier)
        }

        else -> {
            ProfileError(
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
    val context = LocalContext.current
    // 공유할 프로필 URL(App Link)이 아직 없어 안내만 한다. 무반응 버튼으로 두지 않는다.
    val shareNotReady = { Toast.makeText(context, "준비 중이에요", Toast.LENGTH_SHORT).show() }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileCoverHeader(imageUrl = profile.profileImageUrl) {
                ProfileCoverIconButton(
                    iconRes = R.drawable.ic_share_24,
                    contentDescription = "공유",
                    onClick = shareNotReady,
                )
                ProfileCoverIconButton(
                    iconRes = R.drawable.ic_settings_24,
                    contentDescription = "설정",
                    onClick = { navigationHelper.navigateTo(SettingsPage) },
                )
            }
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
            // 소개가 없으면 빈 줄 대신 안내 문구를 옅게 표시해, 편집으로 채울 수 있음을 알린다.
            val hasBio = profile.bio.isNotBlank()
            DsText(
                text = if (hasBio) profile.bio else "아직 소개가 없어요",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = if (hasBio) color.contentDefaultLevel1 else color.contentDefaultLevel3,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
            ProfileStatsRow(
                courseCount = profile.myCourseCount,
                followerCount = profile.followerCount,
                followingCount = profile.followingCount,
                courseLabel = "내 코스",
                onFollowerClick = {
                    navigationHelper.navigateByRoute(FollowListPage.route(FollowListPage.TAB_FOLLOWER))
                },
                onFollowingClick = {
                    navigationHelper.navigateByRoute(FollowListPage.route(FollowListPage.TAB_FOLLOWING))
                },
            )
            ProfileCoursesGrid(
                courses = profile.myCourses,
                onCourseClick = { courseId ->
                    navigationHelper.navigateByRoute(CourseDetailPage.route(courseId))
                },
            )
            Spacer(Modifier.height(20.dp))
        }

        CourmyBottomBar(
            selectedTab = MainTab.MY,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)
                    else -> Unit
                }
            },
        )
    }
}
