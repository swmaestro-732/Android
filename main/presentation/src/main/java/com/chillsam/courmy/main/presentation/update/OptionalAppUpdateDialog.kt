package com.chillsam.courmy.main.presentation.update

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.main.presentation.R as MainR

@Composable
fun OptionalAppUpdateDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    DsConfirmDialog(
        title = stringResource(MainR.string.main_optional_app_update_title),
        description = stringResource(MainR.string.main_optional_app_update_description),
        confirmText = stringResource(MainR.string.main_optional_app_update_confirm),
        dismissText = stringResource(MainR.string.main_optional_app_update_dismiss),
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
