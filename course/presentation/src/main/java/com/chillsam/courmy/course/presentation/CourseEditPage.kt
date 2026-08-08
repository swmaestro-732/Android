package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 코스 편집 화면 진입점. [CourseEditViewModel] 상태를 구독해 [CourseEditScreen] 에 넘긴다.
 *
 * 저장이 끝나면 화면을 닫아 코스 상세로 돌아간다. 상세는 다시 진입할 때 서버에서 새로 불러오므로
 * 편집 결과가 반영된 채로 보인다.
 *
 * 네비게이션 콜백([onClose])은 대상이 다른 모듈(main)에 있어 호출부(AppRouteRegistry)에서 주입한다.
 */
@Composable
fun CourseEditPage(
    viewModel: CourseEditViewModel,
    courseId: Long,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(courseId) {
        viewModel.bind(courseId)
        viewModel.onIntent(CourseEditIntent.Load)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onClose()
    }

    // 불러오기·저장 실패는 화면을 바꾸지 않고 토스트로만 알린다(입력한 값을 잃지 않게).
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(CourseEditIntent.DismissError)
        }
    }

    CourseEditScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onClose = onClose,
        modifier = modifier,
    )
}
