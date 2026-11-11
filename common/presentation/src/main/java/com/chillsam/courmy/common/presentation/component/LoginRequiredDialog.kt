package com.chillsam.courmy.common.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.presentation.R as CommonR

/**
 * 로그인이 필요한 동작(팔로우 등)을 비로그인 상태에서 눌렀을 때 띄우는 안내.
 *
 * 문구를 한 곳에 고정해 화면마다 다르게 묻지 않게 한다. "네"를 고르면 로그인 화면으로
 * 보내는 건 호출부 몫이다([onConfirm]) — 로그인 라우트가 화면마다 다른 모듈에 있다.
 */
@Composable
fun LoginRequiredDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    DsConfirmDialog(
        title = stringResource(CommonR.string.common_login_required_title),
        description = stringResource(CommonR.string.common_login_required_description),
        confirmText = stringResource(CommonR.string.common_login_action),
        dismissText = stringResource(CommonR.string.common_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
