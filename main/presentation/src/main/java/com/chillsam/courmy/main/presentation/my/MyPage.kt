package com.chillsam.courmy.main.presentation.my

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.helper.FeatureFlags
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.course.presentation.component.dashedBorder
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.FollowListPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.domain.settings.SettingsPage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.helper.copyShareLink
import com.chillsam.courmy.main.presentation.profile.ProfileCoursesGrid
import com.chillsam.courmy.main.presentation.profile.ProfileError
import com.chillsam.courmy.main.presentation.profile.ProfileHeader
import com.chillsam.courmy.main.presentation.profile.ProfileHeaderIconButton
import com.chillsam.courmy.main.presentation.profile.ProfileLoading
import com.chillsam.courmy.main.presentation.profile.ProfileSectionLabel
import com.chillsam.courmy.main.domain.user.UserProfilePage as UserProfileRoute

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
            MyContent(
                profile = profile,
                courses = uiState.courses,
                onLoadMore = { viewModel.onIntent(MyProfileIntent.LoadMore) },
                modifier = modifier,
            )
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

/** 빈 상태 카드 높이. 코스 카드 한 줄과 비슷하게 잡아 목록이 채워졌을 때와 자리가 크게 안 바뀌게 한다. */
private val EmptyCourseCardHeight = 140.dp

/**
 * 코스가 하나도 없을 때 "내 코스" 자리에 놓는 만들기 진입점.
 *
 * 빈 화면에 "코스가 없어요" 만 두면 다음에 뭘 할지 알려주지 않는다. 점선 테두리로
 * 아직 비어 있고 채울 수 있는 자리임을 드러내고, 누르면 바로 코스 생성으로 보낸다.
 */
@Composable
private fun CreateFirstCourseCard(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp)
                .height(EmptyCourseCardHeight)
                .clip(RoundedCornerShape(16.dp))
                .dashedBorder(color = color.borderDefaultLevel1, cornerRadius = 16.dp)
                .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add_24),
            contentDescription = null,
            tint = color.contentAccent,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(8.dp))
        DsText(
            text = "나만의 코스 만들기",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentAccent,
        )
        Spacer(Modifier.height(4.dp))
        DsText(
            text = "가 본 곳을 이어 첫 코스를 남겨 보세요",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
    }
}

/** 프로필 로드 성공 시 실제 마이 화면(상단 프로필, 코스 그리드, 하단 탭바). */
@Composable
private fun MyContent(
    profile: MyProfileVO,
    courses: List<ProfileCourseVO>,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    // 내 프로필도 남에게는 타유저 프로필로 열리므로 그 링크를 공유한다.
    val shareProfile = { context.copyShareLink(UserProfileRoute.route(profile.handle)) }
    val scrollState = rememberScrollState()
    // Lazy 목록이 아니라 스크롤 값으로 끝을 판단한다(홈 피드와 같은 헬퍼의 ScrollState 오버로드).
    LoadMoreOnScrollEnd(scrollState, onLoadMore)

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
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
                if (FeatureFlags.SHARE_ENABLED) {
                    ProfileHeaderIconButton(
                        iconRes = R.drawable.ic_share_24,
                        contentDescription = "공유",
                        onClick = shareProfile,
                    )
                }
                ProfileHeaderIconButton(
                    iconRes = R.drawable.ic_settings_24,
                    contentDescription = "설정",
                    onClick = { navigationHelper.navigateTo(SettingsPage) },
                )
            }
            // 프로필과 코스 목록은 성격이 다른 구역이라 선으로 끊는다.
            HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
            ProfileSectionLabel(text = "내 코스 ${profile.myCourseCount}")
            if (courses.isEmpty()) {
                // 코스가 없으면 빈 그리드 대신 만들러 갈 자리를 둔다.
                CreateFirstCourseCard(
                    onClick = { navigationHelper.navigateTo(CourseCreatePage) },
                )
            } else {
                ProfileCoursesGrid(
                    courses = courses,
                    onCourseClick = { courseId ->
                        navigationHelper.navigateByRoute(CourseDetailPage.route(courseId))
                    },
                )
            }
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
