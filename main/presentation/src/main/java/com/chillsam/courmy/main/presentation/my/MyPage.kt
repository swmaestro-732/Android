package com.chillsam.courmy.main.presentation.my

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
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
import com.chillsam.courmy.main.presentation.profile.ProfileError
import com.chillsam.courmy.main.presentation.profile.ProfileHeader
import com.chillsam.courmy.main.presentation.profile.ProfileHeaderIconButton
import com.chillsam.courmy.main.presentation.profile.ProfileLoading
import com.chillsam.courmy.main.presentation.profile.ProfileSectionLabel

/**
 * 마이·프로필 화면(FS-15). [MyViewModel] 이 로드한 프로필 상태에 따라
 * 정상([MyContent]) / 로딩 / 에러 를 분기한다.
 *
 * 상단 프로필·코스 그리드·로딩·에러 모두 `presentation/profile` 의 공용 컴포저블을
 * 타유저 프로필([com.chillsam.courmy.main.presentation.user.UserProfilePage])과 공유한다.
 * 이 화면만의 차이는 우상단 설정 버튼과 소개(bio) 노출, 하단 탭 강조뿐이다.
 */
@Composable
fun MyPage(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    // 프로필 편집에서 돌아왔을 때 수정된 값이 보이도록 다시 보일 때마다 새로 불러온다.
    RefreshOnResume { viewModel.onIntent(MyProfileIntent.Retry) }
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

/** 프로필 로드 성공 시 실제 마이 화면(상단 프로필, 코스 그리드, 하단 탭바). */
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
        ) {
            ProfileHeader(
                imageUrl = profile.profileImageUrl,
                nickname = profile.nickname,
                handle = profile.handle,
                followerCount = profile.followerCount,
                followingCount = profile.followingCount,
                onFollowerClick = {
                    navigationHelper.navigateByRoute(FollowListPage.route(FollowListPage.TAB_FOLLOWER))
                },
                onFollowingClick = {
                    navigationHelper.navigateByRoute(FollowListPage.route(FollowListPage.TAB_FOLLOWING))
                },
                // 마이는 탭으로 들어오는 화면이라 돌아갈 이전 화면이 없다.
                onBack = null,
                bio = profile.bio,
            ) {
                ProfileHeaderIconButton(
                    iconRes = R.drawable.ic_share_24,
                    contentDescription = "공유",
                    onClick = shareNotReady,
                )
                ProfileHeaderIconButton(
                    iconRes = R.drawable.ic_settings_24,
                    contentDescription = "설정",
                    onClick = { navigationHelper.navigateTo(SettingsPage) },
                )
            }
            // 프로필과 코스 목록은 성격이 다른 구역이라 선으로 끊는다.
            HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
            ProfileSectionLabel(text = "내 코스")
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
