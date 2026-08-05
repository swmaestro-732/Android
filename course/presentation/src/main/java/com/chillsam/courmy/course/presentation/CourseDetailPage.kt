package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 코스 상세 화면(FS-11) 진입점. [CourseDetailViewModel] 상태를 구독해
 * 로딩·에러·정상([CourseDetailScreen])을 분기 렌더한다.
 *
 * 네비게이션 콜백([onBack] 등)은 대상 화면이 다른 모듈(main)에 있어 호출부([AppRouteRegistry])에서 주입한다.
 */
@Composable
fun CourseDetailPage(
    viewModel: CourseDetailViewModel,
    courseId: Long,
    onBack: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onFollowAuthor: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(courseId) { viewModel.onIntent(CourseDetailIntent.Load(courseId)) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val detail = uiState.detail

    // 저장 실패는 화면을 바꾸지 않고 토스트로만 알린다.
    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(CourseDetailIntent.ConsumeError)
        }
    }
    when {
        detail != null -> {
            CourseDetailScreen(
                detail = detail,
                isSaving = uiState.isSaving,
                onBack = onBack,
                onAuthorClick = {
                    detail.authorHandle
                        .removePrefix("@")
                        .takeIf(String::isNotBlank)
                        ?.let(onAuthorClick)
                },
                onFollowAuthor = onFollowAuthor,
                onShare = onShare,
                onSaveCourse = { viewModel.onIntent(CourseDetailIntent.ToggleSave) },
                modifier = modifier,
            )
        }

        uiState.isLoading -> {
            CourseDetailLoading(modifier)
        }

        else -> {
            CourseDetailError(
                message = uiState.errorMessage,
                onRetry = { viewModel.onIntent(CourseDetailIntent.Retry) },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CourseDetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
        )
    }
}

@Composable
private fun CourseDetailError(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsText(
                text = message ?: "코스를 불러오지 못했습니다.",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
            )
            DsText(
                text = "다시 시도",
                style = DesignSystemThemeImpl.typeScale.textRegularM,
                color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }
    }
}
