package com.chillsam.courmy.main.presentation.my

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.chillsam.courmy.main.domain.my.FollowListPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.domain.settings.SettingsPage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.profile.ProfileAvatar
import com.chillsam.courmy.main.presentation.profile.ProfileCoursesGrid
import com.chillsam.courmy.main.presentation.profile.ProfileError
import com.chillsam.courmy.main.presentation.profile.ProfileLoading

/**
 * 마이·프로필 화면(FS-15). [MyViewModel] 이 로드한 프로필 상태에 따라
 * 정상([MyContent]) / 로딩 / 에러 를 분기한다.
 *
 * 상단 프로필은 이 화면 전용([MyProfileHeader])이고, 코스 그리드·로딩·에러만
 * `presentation/profile` 의 공용 컴포저블을 타유저 프로필과 공유한다.
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

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
        ) {
            MyProfileHeader(
                profile = profile,
                onShare = shareNotReady,
                onSettings = { navigationHelper.navigateTo(SettingsPage) },
                onFollowerClick = {
                    navigationHelper.navigateByRoute(FollowListPage.route(FollowListPage.TAB_FOLLOWER))
                },
                onFollowingClick = {
                    navigationHelper.navigateByRoute(FollowListPage.route(FollowListPage.TAB_FOLLOWING))
                },
            )
            // 프로필과 코스 목록은 성격이 다른 구역이라 선으로 끊는다.
            HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
            MyCoursesLabel()
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

/** 아바타 사진 지름. 오른쪽 3줄(닉네임·아이디·팔로우 수) 높이와 맞물리는 크기. */
private val AVATAR_SIZE = 92.dp

/** 아바타를 두르는 흰 테두리 두께. */
private val AVATAR_RING = 6.dp

/** 아바타 그림자 높이. 흰 배경 위에서 원의 경계를 만드는 유일한 장치라 넉넉히 준다. */
private val AVATAR_SHADOW_ELEVATION = 14.dp

/**
 * 마이 화면 상단 프로필.
 *
 * 커버 이미지 없이 아바타를 왼쪽에 두고 오른쪽에 닉네임·아이디·팔로우 수를 쌓는다.
 * 소개는 그 아래 전체 폭으로 흐른다. 전부 왼쪽 정렬이며 화면 좌우 여백을 따른다.
 *
 * 타유저 프로필(`UserProfilePage`)은 아직 커버 방식(`ProfileCoverHeader`)을 쓴다 —
 * 이 구성은 마이 화면에만 적용한다.
 */
@Composable
private fun MyProfileHeader(
    profile: MyProfileVO,
    onShare: () -> Unit,
    onSettings: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            HeaderIconButton(
                iconRes = R.drawable.ic_share_24,
                contentDescription = "공유",
                onClick = onShare,
            )
            Spacer(Modifier.width(8.dp))
            HeaderIconButton(
                iconRes = R.drawable.ic_settings_24,
                contentDescription = "설정",
                onClick = onSettings,
            )
        }

        Row(
            // 아이콘 버튼 줄과 바짝 붙인다. 아바타 그림자가 여백처럼 보여서 넉넉히 주면 멀어 보인다.
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RingedAvatar(imageUrl = profile.profileImageUrl)
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                DsText(
                    text = profile.nickname,
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                )
                Spacer(Modifier.height(2.dp))
                DsText(
                    text = "@${profile.handle}",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                )
                Spacer(Modifier.height(10.dp))
                FollowCounts(
                    followerCount = profile.followerCount,
                    followingCount = profile.followingCount,
                    onFollowerClick = onFollowerClick,
                    onFollowingClick = onFollowingClick,
                )
            }
        }

        // 소개가 없으면 빈 줄 대신 안내 문구를 옅게 표시해, 편집으로 채울 수 있음을 알린다.
        val hasBio = profile.bio.isNotBlank()
        DsText(
            text = if (hasBio) profile.bio else "아직 소개가 없어요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = if (hasBio) color.contentDefaultLevel1 else color.contentDefaultLevel3,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp),
        )
    }
}

/**
 * 흰 테두리 + 그림자를 두른 아바타.
 *
 * 화면 배경도 흰색이라 테두리 자체로는 경계가 생기지 않는다 — 원이 떠 보이게 하는 건 전적으로
 * 그림자 몫이라, 커버 위에 얹던 때보다 짙고 넓게 잡는다.
 */
@Composable
private fun RingedAvatar(imageUrl: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .shadow(
                    elevation = AVATAR_SHADOW_ELEVATION,
                    shape = CircleShape,
                    ambientColor = color.contentDefaultLevel0.copy(alpha = 0.32f),
                    spotColor = color.contentDefaultLevel0.copy(alpha = 0.40f),
                ).clip(CircleShape)
                .background(color.bgDefaultLevel1)
                .padding(AVATAR_RING),
    ) {
        ProfileAvatar(imageUrl = imageUrl, size = AVATAR_SIZE)
    }
}

/** 우측 상단 원형 아이콘 버튼. 강조 배경 + 흰 아이콘. */
@Composable
private fun HeaderIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.bgAccent)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = color.contentOnAccent,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** 코스 그리드 위 구역 제목. */
@Composable
private fun MyCoursesLabel() {
    DsText(
        text = "내 코스",
        style = DesignSystemThemeImpl.typeScale.textStrongS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = ScreenHorizontalPadding, end = ScreenHorizontalPadding, top = 20.dp),
    )
}

/** "팔로워 1.4k | 팔로잉 312". 라벨은 옅게, 숫자는 진하게 두어 숫자가 먼저 읽히게 한다. */
@Composable
private fun FollowCounts(
    followerCount: String,
    followingCount: String,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(verticalAlignment = Alignment.CenterVertically) {
        FollowCountItem(label = "팔로워", value = followerCount, onClick = onFollowerClick)
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 14.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(color.borderDefaultLevel1),
        )
        FollowCountItem(label = "팔로잉", value = followingCount, onClick = onFollowingClick)
    }
}

@Composable
private fun FollowCountItem(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
        )
        Spacer(Modifier.width(6.dp))
        DsText(
            text = value,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
    }
}
