package com.chillsam.courmy.main.presentation.settings

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.component.BackTopBar

@Composable
fun NotificationSettingsPage(viewModel: NotificationSettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navigation = LocalNavigationHelper.current
    val notifications = remember(context) { NotificationManagerCompat.from(context) }
    val permissionPreferences = remember(context) { context.getSharedPreferences("notification_permission", 0) }
    var permissionGranted by remember { mutableStateOf(notifications.canShowActivityNotifications()) }
    var enableOnReturn by rememberSaveable { mutableStateOf(false) }
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionGranted = notifications.canShowActivityNotifications()
            if (granted && permissionGranted) {
                viewModel.onIntent(NotificationSettingsIntent.SetEnabled(true))
            } else {
                showPermissionDialog = true
            }
        }
    RefreshOnResume {
        permissionGranted = notifications.canShowActivityNotifications()
        if (enableOnReturn) {
            enableOnReturn = false
            if (permissionGranted) viewModel.onIntent(NotificationSettingsIntent.SetEnabled(true))
        }
        viewModel.onIntent(NotificationSettingsIntent.Refresh)
    }
    NotificationSettingsContent(
        enabled = state.enabled && permissionGranted,
        saving = state.saving,
        onBack = navigation::navigateToBack,
        onCheckedChange = { enabled ->
            when {
                !enabled || permissionGranted -> {
                    viewModel.onIntent(NotificationSettingsIntent.SetEnabled(enabled))
                }

                notifications.canRequestPermission(permissionPreferences.getBoolean("requested", false)) -> {
                    permissionPreferences.edit().putBoolean("requested", true).apply()
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    showPermissionDialog = true
                }
            }
        },
    )
    if (showPermissionDialog || state.error) {
        NotificationPermissionDialog(
            error = state.error,
            onConfirm = {
                enableOnReturn = !state.error
                showPermissionDialog = false
                viewModel.onIntent(NotificationSettingsIntent.DismissError)
                if (!context.openNotificationSettings()) enableOnReturn = false
            },
            onDismiss = {
                showPermissionDialog = false
                viewModel.onIntent(NotificationSettingsIntent.DismissError)
            },
        )
    }
}

private fun Context.openNotificationSettings(): Boolean {
    val intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        }
    return runCatching { startActivity(intent) }
        .onFailure {
            Toast.makeText(this, "기기 알림 설정을 열 수 없어요.", Toast.LENGTH_SHORT).show()
        }.isSuccess
}

private fun NotificationManagerCompat.canRequestPermission(requested: Boolean): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !areNotificationsEnabled() && !requested

private fun NotificationManagerCompat.canShowActivityNotifications(): Boolean =
    areNotificationsEnabled() &&
        (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                getNotificationChannel("courmy_activity")?.importance != NotificationManager.IMPORTANCE_NONE
        )

@Composable
private fun NotificationPermissionDialog(
    error: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    DsConfirmDialog(
        title = if (error) "알림 설정을 완료하지 못했어요" else "기기 알림을 허용해 주세요",
        description =
            if (error) {
                "네트워크 연결을 확인하고 다시 시도해 주세요. 알림을 바로 차단하려면 기기 설정에서 꺼 주세요."
            } else {
                "전체 알림을 받으려면 기기 설정에서 Courmy 알림을 허용해 주세요."
            },
        confirmText = "기기 설정",
        dismissText = "닫기",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}

@Composable
internal fun NotificationSettingsContent(
    enabled: Boolean,
    saving: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(Modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "알림", onBack = onBack, boxed = true)
        Row(
            Modifier
                .fillMaxWidth()
                .toggleable(value = enabled, enabled = !saving, role = Role.Switch, onValueChange = onCheckedChange)
                .padding(horizontal = ScreenHorizontalPadding, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = "전체 알림",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel0,
                modifier = Modifier.weight(1f),
            )
            DsSwitch(
                checked = enabled,
                onCheckedChange = onCheckedChange,
                enabled = !saving,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
        SettingsDivider()
    }
}

@Preview
@Composable
private fun NotificationSettingsPreview() {
    DesignSystemTheme { NotificationSettingsContent(true, false, {}, {}) }
}
