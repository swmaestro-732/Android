package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.util.formatCreatedAt
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/** 임시: 인메모리 저장코스엔 서버 courseId 가 없어, 상세 확인용으로 공개 코스 하나에 연결한다. */
private const val TEMP_PUBLIC_COURSE_ID = "1"

/**
 * 마이 화면(하단 네비게이션 목적지). 이번 실행 세션에서 저장([MyViewModel.savedCourses])한
 * 코스를 리스트(제목 + 생성 시각)로 보여주고, 하단 탭바를 함께 노출한다.
 */
@Composable
fun MyPage(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val courses by viewModel.savedCourses.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
        ) {
            DsText(
                text = "저장한 코스",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
                modifier = Modifier.padding(vertical = 20.dp),
            )

            if (courses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    DsText(
                        text = "저장한 코스가 없어요",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(courses) { course ->
                        SavedCourseRow(
                            title = course.title,
                            createdAtMillis = course.createdAtMillis,
                            onClick = {
                                // TODO(Phase 3): 실제 저장 코스의 courseId 로 교체. 지금은 인메모리 저장코스에
                                //  서버 id 가 없어, 공개 코스로 임시 연결해 상세(API)를 확인한다.
                                navigationHelper.navigateByRoute(
                                    CourseDetailPage.route(TEMP_PUBLIC_COURSE_ID),
                                )
                            },
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
                        )
                    }
                }
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.MY,
            onTabSelected = { tab ->
                if (tab == MainTab.HOME) navigationHelper.navigateTo(HomePage)
            },
        )
    }
}

@Composable
private fun SavedCourseRow(
    title: String,
    createdAtMillis: Long,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        DsText(
            text = formatCreatedAt(createdAtMillis),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
    }
}
