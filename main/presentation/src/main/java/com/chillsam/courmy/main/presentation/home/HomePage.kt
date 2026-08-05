package com.chillsam.courmy.main.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.course.domain.DraftListPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.BottomBarHeight
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.HomeCourseCard
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.my.MyViewModel
import com.chillsam.courmy.main.presentation.profile.ProfileAvatar

/**
 * 앱의 기본 시작 화면(FS-09). 헤더(위치·날씨·인사) + 공개 코스 피드로 구성한다.
 *
 * 피드는 `GET /service/v1/courses`(공개 엔드포인트)라 로그인 여부와 무관하게 노출한다.
 * 헤더의 위치·날씨는 아직 고정값이다. [wiki-needed]
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    // 공개 코스 피드(GET /service/v1/courses). 공개 엔드포인트라 게스트에서도 그대로 부른다.
    val feedViewModel: HomeFeedViewModel = hiltViewModel()
    val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()
    // 헤더 인사말·아바타는 내 프로필에서 가져온다.
    val profile = if (session.isLoggedIn) myProfileForHeader() else null

    // 저장 실패는 화면을 바꾸지 않고 토스트로만 알린다.
    LaunchedEffect(feedState.actionErrorMessage) {
        feedState.actionErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            feedViewModel.onIntent(HomeFeedIntent.ConsumeError)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding =
                PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = BottomBarHeight + 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { HomeHeader(nickname = profile?.nickname.orEmpty(), imageUrl = profile?.profileImageUrl.orEmpty()) }
            // 헤더·하단 탭은 늘 보여야 하므로, 로딩·에러는 화면 전체가 아니라 피드 자리에서만 분기한다.
            when {
                feedState.courses.isNotEmpty() -> {
                    items(feedState.courses, key = { it.id }) { course ->
                        HomeCourseCard(
                            course = course,
                            isSaved = course.id in feedState.savedCourseIds,
                            onClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(course.id)) },
                            onBookmarkClick = { feedViewModel.onIntent(HomeFeedIntent.ToggleSave(course.id)) },
                        )
                    }
                }

                feedState.isLoading -> {
                    item { FeedLoading() }
                }

                else -> {
                    item {
                        FeedMessage(
                            message = feedState.errorMessage ?: "아직 공개된 코스가 없어요.",
                            onRetry = feedState.errorMessage?.let { { feedViewModel.onIntent(HomeFeedIntent.Retry) } },
                        )
                    }
                }
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.HOME,
            onTabSelected = { tab ->
                when (tab) {
                    // 로그인 전이면 게스트 마이, 로그인 후면 마이·프로필로 분기.
                    MainTab.MY -> navigationHelper.navigateTo(if (session.isLoggedIn) MyPage else GuestMyPage)

                    MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)

                    else -> Unit
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        // 코스 만들기 FAB 는 로그인 상태에서만 노출한다.
        if (session.isLoggedIn) {
            CreateCourseMenu(
                expanded = menuExpanded,
                onToggle = { menuExpanded = !menuExpanded },
                onNewCourse = {
                    menuExpanded = false
                    navigationHelper.navigateTo(CourseCreatePage)
                },
                onLoadDraft = {
                    menuExpanded = false
                    navigationHelper.navigateTo(DraftListPage)
                },
            )
        }
    }
}

/** 피드 로딩 자리. 헤더 아래 카드 영역만 차지한다. */
@Composable
private fun FeedLoading() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = DesignSystemThemeImpl.designSystemColor.contentAccent)
    }
}

/** 피드가 비었거나 실패했을 때의 안내. [onRetry] 가 있으면 재시도를 노출한다. */
@Composable
private fun FeedMessage(
    message: String,
    onRetry: (() -> Unit)?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsText(
            text = message,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
        )
        if (onRetry != null) {
            DsText(
                text = "다시 시도",
                style = DesignSystemThemeImpl.typeScale.textRegularM,
                color = color.contentAccent,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }
    }
}

/**
 * 홈 헤더용 내 프로필.
 *
 * [MyViewModel] 은 생성 즉시 `GET /service/v1/mypage` 를 호출하므로 **로그인 상태에서만** 만든다.
 * 게스트도 보는 홈에서 무조건 만들면 JWT 없이 요청이 나가 401 로 실패하고,
 * refreshToken 도 없어 TokenAuthenticator 가 재시도를 포기한다.
 */
@Composable
private fun myProfileForHeader(): MyProfileVO? {
    val myViewModel: MyViewModel = hiltViewModel()
    val myState by myViewModel.uiState.collectAsStateWithLifecycle()
    return myState.profile
}

/** 헤더 코스 개수 안내(더미 고정값). */
private const val NEARBY_COURSE_COUNT = 42

/** 홈 헤더: 위치·날씨 → 인사 타이틀 + 우상단 아바타 → 코스 개수 안내(더미 고정값). */
@Composable
private fun HomeHeader(
    nickname: String,
    imageUrl: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                DsText(
                    text = "성수동 · 흐림 18°",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
                DsText(
                    text = "${nickname.ifBlank { "회원" }}님, 오늘은\n어디로 떠나볼까요?",
                    modifier = Modifier.padding(top = 6.dp),
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                    maxLines = 2,
                )
            }
            ProfileAvatar(
                imageUrl = imageUrl,
                size = 44.dp,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
        Row(modifier = Modifier.padding(top = 16.dp)) {
            DsText(
                text = "성수동 주변 · 오늘 날씨에 맞는 코스 ",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
            DsText(
                text = NEARBY_COURSE_COUNT.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel0,
            )
        }
    }
}
