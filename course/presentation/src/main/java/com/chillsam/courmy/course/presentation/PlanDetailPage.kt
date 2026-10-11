package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
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
    val color = DesignSystemThemeImpl.designSystemColor
    val detail = state.detail
    if (detail != null) {
        PlanDetailScreen(detail, onBack, onSourceCourse, modifier)
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
            CircularProgressIndicator(modifier = Modifier.padding(24.dp), color = color.contentAccent)
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
