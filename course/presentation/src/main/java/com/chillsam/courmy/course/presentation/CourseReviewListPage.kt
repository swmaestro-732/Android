package com.chillsam.courmy.course.presentation

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 코스 후기 전체보기(FS-37) 진입점. [CourseReviewListViewModel] 상태를 구독해
 * 로딩·에러·정상([CourseReviewListScreen])을 분기 렌더한다.
 */
@Composable
fun CourseReviewListPage(
    viewModel: CourseReviewListViewModel,
    courseId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(courseId) { viewModel.onIntent(CourseReviewListIntent.Load(courseId)) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary
    when {
        summary != null -> {
            CourseReviewListScreen(
                summary = summary,
                reviews = uiState.reviews,
                sort = uiState.sort,
                onBack = onBack,
                onSortChange = { viewModel.onIntent(CourseReviewListIntent.ChangeSort(it)) },
                modifier = modifier,
            )
        }

        uiState.isLoading -> {
            ReviewListLoading(modifier)
        }

        else -> {
            ReviewListError(
                message = uiState.errorMessage,
                onRetry = { viewModel.onIntent(CourseReviewListIntent.Retry) },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ReviewListLoading(modifier: Modifier = Modifier) {
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
private fun ReviewListError(
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
                text = message ?: "후기를 불러오지 못했습니다.",
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
