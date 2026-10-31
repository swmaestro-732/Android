package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 따라가기 화면(FS-21) 라우팅 진입점. [courseId] 코스를 로드해 단계별 화면을 그린다. */
@Composable
fun CourseFollowPage(
    courseId: Long,
    actions: CourseFollowPageActions,
    modifier: Modifier = Modifier,
    viewModel: CourseFollowViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val color = DesignSystemThemeImpl.designSystemColor

    LaunchedEffect(courseId) {
        viewModel.onIntent(CourseFollowIntent.Load(courseId))
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(CourseFollowIntent.ConsumeError)
        }
    }

    BackHandler(enabled = uiState.isRecording) {}

    if (uiState.detail != null) {
        CourseFollowScreen(
            state = uiState,
            onIntent = viewModel::onIntent,
            onBack = { if (!uiState.isRecording) actions.onBack() },
            onLeaveReview = { if (!uiState.isRecording) actions.onLeaveReview() },
            modifier = modifier,
        )
    } else {
        // 코스를 아직 못 받았거나 실패한 상태. 지도·동선이 없으면 그릴 게 없어 안내만 둔다.
        Column(
            modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (uiState.isLoading) {
                CourseFollowSkeleton()
            } else {
                DsText(
                    text = "코스를 불러오지 못했어요.",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                )
                DsText(
                    text = "다시 시도",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentOnAccent,
                    modifier =
                        Modifier
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.bgAccent)
                            .clickable { viewModel.onIntent(CourseFollowIntent.Retry) }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                )
            }
        }
    }
}
