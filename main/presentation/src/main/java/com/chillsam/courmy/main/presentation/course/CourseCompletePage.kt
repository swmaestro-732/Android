package com.chillsam.courmy.main.presentation.course

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.course.presentation.CourseCompleteScreen
import com.chillsam.courmy.course.presentation.sampleCourseComplete
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.MyPage

/**
 * 코스 저장 완료 화면 진입점. course 모듈의 [CourseCompleteScreen](Figma FS-34-Done) 을 렌더링한다.
 * ✕ 는 홈으로, "내 코스에서 보기" 는 마이 화면으로 이동한다.
 */
@Composable
fun CourseCompletePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    CourseCompleteScreen(
        course = sampleCourseComplete(),
        onClose = { navigationHelper.navigateTo(HomePage) },
        onViewMyCourses = { navigationHelper.navigateTo(MyPage) },
        modifier = modifier,
    )
}
