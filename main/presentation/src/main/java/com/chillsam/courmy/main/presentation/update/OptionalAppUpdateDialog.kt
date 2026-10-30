package com.chillsam.courmy.main.presentation.update

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme

@Composable
fun OptionalAppUpdateDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    DsConfirmDialog(
        title = "새 버전이 있어요",
        description = "더 나아진 Courmy를 만나보세요.\n마이 > 설정 > 앱 버전에서도 확인할 수 있어요.",
        confirmText = "업데이트",
        dismissText = "나중에",
        onConfirm = {
            onAccept()
            context.openStoreListing()
        },
        onDismiss = onDismiss,
    )
}

@Preview(showBackground = true)
@Composable
private fun OptionalAppUpdateDialogPreview() {
    DesignSystemTheme {
        OptionalAppUpdateDialog(onAccept = {}, onDismiss = {})
    }
}
