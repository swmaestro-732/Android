package com.chillsam.courmy.main.presentation.user

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.LoginRequiredDialog
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.entity.user.UserProfileVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.profile.ProfileCoursesGrid
import com.chillsam.courmy.main.presentation.profile.ProfileError
import com.chillsam.courmy.main.presentation.profile.ProfileHeader
import com.chillsam.courmy.main.presentation.profile.ProfileHeaderIconButton
import com.chillsam.courmy.main.presentation.profile.ProfileLoading
import com.chillsam.courmy.main.presentation.profile.ProfileSectionLabel

/**
 * 타유저 프로필 화면(FS-15 OtherUserPageActivity).
 * 상단 프로필·코스 그리드는 마이 화면과 `presentation/profile` 공용 컴포저블을 그대로 공유해
 * 두 화면이 같은 모습을 갖는다. 이 화면만의 차이는 좌상단 뒤로 가기와 팔로우 버튼,
 * 그리고 소개(bio)가 없다는 점이다 — 서버 응답에 필드가 없다([UserProfileVO] 주석 참고).
 *
 * 조회 키는 handle 이며(`GET /service/v1/mypage/{handle}`), 라우트 인자로 받아 로드한다.
 */
@Composable
fun UserProfilePage(
    handle: String,
    modifier: Modifier = Modifier,
    viewModel: UserProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(handle) {
        viewModel.onIntent(UserProfileIntent.Load(handle))
    }
    // 팔로우하고 돌아오면 팔로워 수·관계가 바뀌어 있을 수 있어 다시 보일 때마다 새로 불러온다.
    RefreshOnResume { viewModel.onIntent(UserProfileIntent.Retry) }
    LaunchedEffect(uiState.followErrorMessage) {
        uiState.followErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(UserProfileIntent.ConsumeFollowError)
        }
    }

    // 비로그인 상태로 팔로우를 누른 경우. 안내를 닫는 건 어느 쪽을 골라도 같다.
    val navigationHelper = LocalNavigationHelper.current
    if (uiState.needsLogin) {
        LoginRequiredDialog(
            onConfirm = {
                viewModel.onIntent(UserProfileIntent.ConsumeLoginRequired)
                navigationHelper.navigateTo(LoginPage)
            },
            onDismiss = { viewModel.onIntent(UserProfileIntent.ConsumeLoginRequired) },
        )
    }

    val profile = uiState.profile
    when {
        profile != null -> {
            UserProfileContent(
                profile = profile,
                isFollowInFlight = uiState.isFollowInFlight,
                onToggleFollow = { viewModel.onIntent(UserProfileIntent.ToggleFollow) },
                modifier = modifier,
            )
        }

        uiState.isLoading -> {
            ProfileLoading(modifier = modifier)
        }

        else -> {
            ProfileError(
                message = uiState.errorMessage,
                onRetry = { viewModel.onIntent(UserProfileIntent.Retry) },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun UserProfileContent(
    profile: UserProfileVO,
    isFollowInFlight: Boolean,
    onToggleFollow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    // 공유 URL(App Link)과 타유저 팔로우 목록 조회가 아직 없어 안내만 한다. 무반응 버튼으로 두지 않는다.
    val notReady = { Toast.makeText(context, "준비 중이에요", Toast.LENGTH_SHORT).show() }

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
                // 팔로워·팔로잉 목록은 아직 내 계정 것만 조회할 수 있어 타유저 화면에서는 이동하지 않는다.
                onFollowerClick = notReady,
                onFollowingClick = notReady,
                onBack = { navigationHelper.navigateToBack() },
                // 서버가 타유저 소개를 주지 않아 소개 줄 자체를 두지 않는다.
                bio = null,
            ) {
                ProfileHeaderIconButton(
                    iconRes = R.drawable.ic_share_24,
                    contentDescription = "공유",
                    onClick = notReady,
                )
            }
            // 자기 자신을 연 경우(응답 id == 내 id)에는 팔로우 버튼을 노출하지 않는다.
            if (!profile.isMe) {
                FollowButton(
                    relation = profile.relation,
                    onClick = onToggleFollow,
                    inFlight = isFollowInFlight,
                    modifier =
                        Modifier.padding(
                            start = ScreenHorizontalPadding,
                            end = ScreenHorizontalPadding,
                            bottom = 20.dp,
                        ),
                )
            }
            // 프로필과 코스 목록은 성격이 다른 구역이라 선으로 끊는다.
            HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
            ProfileSectionLabel(text = "코스 ${profile.courseCount}")
            ProfileCoursesGrid(
                courses = profile.courses,
                onCourseClick = { courseId ->
                    navigationHelper.navigateByRoute(CourseDetailPage.route(courseId))
                },
            )
            Spacer(Modifier.height(20.dp))
        }

        // 타유저 프로필은 어느 탭에도 속하지 않으므로 아무 탭도 강조하지 않는다.
        CourmyBottomBar(
            selectedTab = null,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)
                    MainTab.MY -> navigationHelper.navigateTo(MyPage)
                }
            },
        )
    }
}
