package com.chillsam.courmy.common.presentation.report

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportTargetType
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 실제 지원되는 대상 ID를 명시하여 연다. 장소·후기를 다른 대상으로 대체하지 않는다. */
@Composable
fun ReportAction(
    targetType: ReportTargetType,
    targetId: Long,
) {
    var expanded by remember(targetType, targetId) { mutableStateOf(false) }
    var reporting by remember(targetType, targetId) { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, enabled = targetId > 0) {
            Icon(
                painterResource(R.drawable.ic_more_vert_24),
                contentDescription = "더 보기",
                tint = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { DsText(text = "신고하기") }, onClick = {
                expanded = false
                reporting = true
            })
        }
    }
    if (reporting) {
        ReportDialog(targetType, targetId, onClose = { reporting = false })
    }
}

@Composable
private fun ReportDialog(
    targetType: ReportTargetType,
    targetId: Long,
    onClose: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel(key = "report-$targetType-$targetId"),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val close = {
        if (!state.submitting) {
            viewModel.onIntent(ReportIntent.Reset)
            onClose()
        }
    }
    LaunchedEffect(state.success) {
        state.success?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(ReportIntent.Reset)
            onClose()
        }
    }
    Dialog(
        onDismissRequest = close,
        properties =
            DialogProperties(
                dismissOnClickOutside = !state.submitting,
                dismissOnBackPress = !state.submitting,
            ),
    ) {
        BackHandler(enabled = state.submitting) {}
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1, RoundedCornerShape(20.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsText(text = "신고하기", style = DesignSystemThemeImpl.typeScale.titleExtraL)
            DsText(text = "신고 사유를 선택해 주세요.")
            ReportReason.entries.forEach { reason ->
                DsChip(text = reason.label, selected = state.reason == reason, enabled = !state.submitting, onClick = {
                    viewModel.onIntent(ReportIntent.SelectReason(reason))
                })
            }
            DsTextField(value = state.description, onValueChange = {
                viewModel.onIntent(ReportIntent.Describe(it))
            }, enabled = !state.submitting, singleLine = false, placeholder = "추가 설명 (선택, 최대 500자)")
            state.error?.let {
                DsText(
                    text = it,
                    color = DesignSystemThemeImpl.designSystemColor.contentDanger,
                    maxLines = Int.MAX_VALUE,
                )
            }
            DsButton(
                text = "신고 접수",
                enabled = state.reason != null && !state.submitting,
                loading = state.submitting,
                onClick = { viewModel.onIntent(ReportIntent.Submit(targetType, targetId)) },
            )
            DsButton(text = "취소", variant = DsButtonVariant.Secondary, enabled = !state.submitting, onClick = close)
        }
    }
}
