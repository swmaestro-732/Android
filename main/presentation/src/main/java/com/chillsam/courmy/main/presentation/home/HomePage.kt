package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.course.domain.DraftListPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.component.SavedCourseCard

/**
 * 앱의 기본 시작 화면.
 *
 * 코스 만들기 진입 플로우(FS-09) 작업 단계라, 본문은 저장함과 동일한 코스 카드 1개만 두고
 * 하단 탭바([CourmyBottomBar])와 코스 만들기 FAB([CreateCourseMenu])를 우선 구현한다.
 * 코스 카드는 로그인 여부와 무관하게 노출한다.
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    var menuExpanded by remember { mutableStateOf(false) }
    // 로그인 전에도 볼 수 있는 대표 코스 카드(백엔드 연동 전 더미).
    val featuredCourse = remember { SavedCourseVO.sample.first() }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
        ) {
            SavedCourseCard(
                course = featuredCourse,
                onClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(featuredCourse.id)) },
                onBookmarkClick = {},
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        CourmyBottomBar(
            selectedTab = MainTab.HOME,
            onTabSelected = { tab ->
                // 프로토타입: 마이·저장 탭만 네비게이션 연결. 나머지 탭은 화면이 붙을 때 연결.
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
