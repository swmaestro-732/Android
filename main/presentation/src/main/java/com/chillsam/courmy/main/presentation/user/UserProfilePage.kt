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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.entity.user.UserProfileVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.profile.ProfileCoursesGrid
import com.chillsam.courmy.main.presentation.profile.ProfileCoverHeader
import com.chillsam.courmy.main.presentation.profile.ProfileCoverIconButton
import com.chillsam.courmy.main.presentation.profile.ProfileError
import com.chillsam.courmy.main.presentation.profile.ProfileLoading
import com.chillsam.courmy.main.presentation.profile.ProfileStatsRow

/**
 * 타유저 프로필 화면(FS-15 OtherUserPageActivity).
 * 커버·통계·코스 그리드는 마이 화면과 `presentation/profile` 공용 컴포저블을 공유하고,
 * 상단 액션은 공유 버튼만, 통계 아래에는 팔로우 버튼([FollowButton])이 붙는다.
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
            // Figma 는 여기에 한 줄 소개(bio)가 있으나 서버 응답에 필드가 없어 생략한다(UserProfileVO 주석 참고).
            Spacer(Modifier.height(12.dp))
            ProfileStatsRow(
                courseCount = profile.courseCount,
                followerCount = profile.followerCount,
                followingCount = profile.followingCount,
                courseLabel = "코스",
            )
            // 자기 자신을 연 경우(응답 id == 내 id)에는 팔로우 버튼을 노출하지 않는다.
            if (!profile.isMe) {
                FollowButton(
                    relation = profile.relation,
                    onClick = onToggleFollow,
                    inFlight = isFollowInFlight,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
                )
            } else {
                Spacer(Modifier.height(16.dp))
            }
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
