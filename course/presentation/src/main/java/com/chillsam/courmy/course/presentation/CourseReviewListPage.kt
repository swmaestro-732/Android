package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.ContentSkeleton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 코스 후기 전체보기(FS-37) 진입점. [CourseReviewListViewModel] 상태를 구독해
 * 로딩·에러·정상([CourseReviewListScreen])을 분기 렌더한다.
 *
 * 후기 작성([CourseReviewWriteScreen])은 **이 라우트 위에 덮는 화면**으로 띄운다.
 * 별도 라우트로 빼면 작성을 마치고 돌아왔을 때 "방금 썼다"는 사실을 목록에 전할 길이 없어
 * 새로 쓴 후기가 보이지 않는다. 여기서 띄우면 성공 즉시 목록을 다시 받을 수 있다.
 */
@Composable
fun CourseReviewListPage(
    viewModel: CourseReviewListViewModel,
    courseId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 상단 바("따라간 후기")가 흰색이라 상태바도 같은 색으로 이어 붙인다. 기본 회색이면 띠만 분리돼 보인다.
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    LaunchedEffect(courseId) { viewModel.onIntent(CourseReviewListIntent.Load(courseId)) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isWriting by rememberSaveable { mutableStateOf(false) }

    // 스낵바 대신 토스트를 쓴다 — 이 화면에는 Scaffold 가 없고, 알릴 것은 한 줄짜리 결과 통지뿐이다.
    val context = LocalContext.current
    LaunchedEffect(uiState.message) {
        val message = uiState.message ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.onIntent(CourseReviewListIntent.ConsumeMessage)
    }

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
                isLoadingMore = uiState.isLoadingMore,
                deletingReviewId = uiState.deletingReviewId,
                canWriteReview = uiState.canWriteReview,
                onLoadMore = { viewModel.onIntent(CourseReviewListIntent.LoadMore) },
                onDeleteReview = { viewModel.onIntent(CourseReviewListIntent.DeleteReview(it)) },
                onWriteReview = { isWriting = true },
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

    if (isWriting) {
        CourseReviewWriteOverlay(
            viewModel = hiltViewModel(),
            courseId = courseId,
            onClose = { isWriting = false },
            onWritten = {
                isWriting = false
                viewModel.onIntent(CourseReviewListIntent.ReloadAfterWrite)
            },
        )
    }
}

/**
 * 후기 작성 오버레이. 작성이 끝나면 [onWritten] 으로 알려 목록이 스스로 갱신하게 한다.
 *
 * 작성 ViewModel 은 이 컴포저블이 아니라 **라우트**에 묶여 있어 오버레이를 닫아도 살아남는다.
 * 그래서 성공 표시를 받아 간 즉시 [CourseReviewWriteIntent.ConsumeSubmitted] 로 비운다 —
 * 남겨 두면 다음에 작성 화면을 열자마자 이 효과가 다시 발화해 화면이 곧바로 닫힌다.
 */
@Composable
private fun CourseReviewWriteOverlay(
    viewModel: CourseReviewWriteViewModel,
    courseId: Long,
    onClose: () -> Unit,
    onWritten: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.submittedReviewId) {
        if (uiState.submittedReviewId == 0L) return@LaunchedEffect
        onWritten()
        viewModel.onIntent(CourseReviewWriteIntent.ConsumeSubmitted)
    }
    CourseReviewWriteScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onSubmit = { viewModel.onIntent(CourseReviewWriteIntent.Submit(courseId)) },
        onClose = onClose,
    )
}

@Composable
private fun ReviewListLoading(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        ContentSkeleton()
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
