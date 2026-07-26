package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.domain.DraftListPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 앱의 기본 시작 화면.
 *
 * 코스 만들기 진입 플로우(FS-09) 작업 단계라, 본문은 아직 "Home" 플레이스홀더 하나만 두고
 * 하단 탭바([HomeBottomBar])와 코스 만들기 FAB([CreateCourseMenu])를 우선 구현한다.
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
    ) {
        // 본문 플레이스홀더 — 실제 홈 콘텐츠는 이후 단계에서 채운다.
        DsText(
            text = "Home",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            modifier = Modifier.align(Alignment.Center),
        )

        CourmyBottomBar(
            selectedTab = MainTab.HOME,
            onTabSelected = { tab ->
                // 프로토타입: 마이 탭만 네비게이션 연결. 나머지 탭은 화면이 붙을 때 연결.
                if (tab == MainTab.MY) navigationHelper.navigateTo(MyPage)
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

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
