package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.ContentSkeleton
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@Composable
fun PlanDetailPage(
    planId: Long,
    onBack: () -> Unit,
    onSourceCourse: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(planId) { viewModel.onIntent(PlanDetailIntent.Load(planId)) }

    // 수정·삭제 실패 등 일회성 안내. 이 화면에는 Scaffold 가 없어 토스트로 알린다.
    val context = LocalContext.current
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.onIntent(PlanDetailIntent.ConsumeMessage)
    }
    // 삭제가 확정되면 상세에 머물 이유가 없다 — 목록(마이 계획 탭)으로 되돌아간다.
    LaunchedEffect(state.deleted) { if (state.deleted) onBack() }

    val color = DesignSystemThemeImpl.designSystemColor
    val detail = state.detail
    if (detail != null) {
        PlanDetailScreen(
            detail = detail,
            onBack = onBack,
            onSourceCourse = onSourceCourse,
            onEdit = { title, memo, date -> viewModel.onIntent(PlanDetailIntent.Edit(title, memo, date)) },
            onDelete = { viewModel.onIntent(PlanDetailIntent.Delete) },
            isMutating = state.isMutating,
            modifier = modifier,
        )
        return
    }
    Column(modifier.fillMaxSize().background(color.bgDefaultLevel0).statusBarsPadding()) {
        IconButton(onClick = onBack) {
            Icon(
                painterResource(R.drawable.arrow_back_24px),
                contentDescription = "뒤로",
                tint = color.contentDefaultLevel0,
            )
        }
        if (state.isLoading) {
            ContentSkeleton()
        } else if (state.errorMessage != null) {
            Column(Modifier.padding(20.dp)) {
                DsText(
                    state.errorMessage.orEmpty(),
                    style = DesignSystemThemeImpl.typeScale.textRegularM,
                    color = color.contentDefaultLevel0,
                )
                DsButton(text = "다시 시도", onClick = { viewModel.onIntent(PlanDetailIntent.Retry) })
            }
        }
    }
}
