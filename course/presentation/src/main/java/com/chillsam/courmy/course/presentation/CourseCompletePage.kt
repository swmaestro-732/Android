package com.chillsam.courmy.course.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 코스 저장 완료 화면 진입점. [CourseCompleteScreen](Figma FS-34-Done) 을 렌더링한다.
 * 방금 저장한 코스([CourseCompleteViewModel.lastCompleted])를 보여주고, 없으면 예시로 폴백한다.
 *
 * ✕([onClose])·"내 코스에서 보기"([onViewMyCourses])의 목적지 화면은 다른 모듈(main)에 있어
 * course 모듈이 직접 참조할 수 없으므로, 호출부([AppRouteRegistry])에서 콜백으로 주입한다.
 */
@Composable
fun CourseCompletePage(
    viewModel: CourseCompleteViewModel,
    onClose: () -> Unit,
    onViewMyCourses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lastCompleted by viewModel.lastCompleted.collectAsStateWithLifecycle()
    CourseCompleteScreen(
        course = lastCompleted ?: sampleCourseComplete(),
        onClose = onClose,
        onViewMyCourses = onViewMyCourses,
        modifier = modifier,
    )
}
