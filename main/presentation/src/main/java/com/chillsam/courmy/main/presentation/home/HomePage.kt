package com.chillsam.courmy.main.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.presentation.component.BottomBarHeight
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.HomeCourseCard
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 앱의 기본 시작 화면(FS-09). 헤더(위치·날씨·인사) + 공개 코스 피드로 구성한다.
 * 피드·헤더 값은 백엔드 연동 전까지 더미 고정값이며, 코스 카드는 로그인 여부와 무관하게 노출한다.
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    // 더미 공개 코스 피드(백엔드 미연동). 실제 홈 피드 API 연동 시 SavedPage 와 함께
    // domain UseCase + ViewModel 로 전환한다(현재는 SavedPage 와 동일하게 presentation 에서 더미 참조). [wiki-needed]
    val courses = remember { HomeCourseVO.sample }
    // 저장(북마크) 토글은 아직 미연동이라 안내만 한다.
    val notReady = { Toast.makeText(context, "준비 중이에요", Toast.LENGTH_SHORT).show() }

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
            item { HomeHeader() }
            items(courses) { course ->
                HomeCourseCard(
                    course = course,
                    onClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(course.id)) },
                    onBookmarkClick = notReady,
                )
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

/** 헤더 코스 개수 안내(더미 고정값). */
private const val NEARBY_COURSE_COUNT = 42

/** 홈 헤더: 위치·날씨 → 인사 타이틀 + 우상단 아바타 → 코스 개수 안내(더미 고정값). */
@Composable
private fun HomeHeader() {
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
                    text = "지호님, 오늘은\n어디로 떠나볼까요?",
                    modifier = Modifier.padding(top = 6.dp),
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                    maxLines = 2,
                )
            }
            Box(
                modifier =
                    Modifier
                        .padding(start = 12.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.imagePlaceholder),
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
