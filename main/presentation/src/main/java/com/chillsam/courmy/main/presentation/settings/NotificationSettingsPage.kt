package com.chillsam.courmy.main.presentation.settings

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.notification.NotificationToggleRow
import com.chillsam.courmy.main.presentation.notification.canRequestNotificationPermission
import com.chillsam.courmy.main.presentation.notification.canShowActivityNotifications
import com.chillsam.courmy.main.presentation.notification.openNotificationSettings

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
        marketing = state.marketing,
        night = state.night,
        saving = state.saving,
        onBack = navigation::navigateToBack,
        onCheckedChange = { enabled ->
            when {
                !enabled || permissionGranted -> {
                    viewModel.onIntent(NotificationSettingsIntent.SetEnabled(enabled))
                }

                notifications.canRequestNotificationPermission(
                    permissionPreferences.getBoolean("requested", false),
                ) -> {
                    permissionPreferences.edit().putBoolean("requested", true).apply()
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    showPermissionDialog = true
                }
            }
        },
        onMarketingChange = { viewModel.onIntent(NotificationSettingsIntent.SetMarketing(it)) },
        onNightChange = { viewModel.onIntent(NotificationSettingsIntent.SetNight(it)) },
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
                "활동 알림을 받으려면 기기 설정에서 Courmy 알림을 허용해 주세요."
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
    marketing: Boolean,
    night: Boolean,
    saving: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onMarketingChange: (Boolean) -> Unit,
    onNightChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(Modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "알림", onBack = onBack, boxed = true)
        // 홈 최초 진입 동의 팝업과 같은 항목·문구를 쓴다. 여기서는 모두 켜고 끌 수 있다.
        NotificationToggleRow(
            title = "활동 알림",
            description = "내 코스의 좋아요와 댓글 소식을 받아요",
            checked = enabled,
            onToggle = { onCheckedChange(!enabled) },
            enabled = !saving,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
        NotificationToggleRow(
            title = "광고 알림",
            description = "추천 코스와 이벤트 소식을 받아요",
            checked = marketing,
            onToggle = { onMarketingChange(!marketing) },
            enabled = !saving,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
        NotificationToggleRow(
            title = "야간에도 받기",
            description = "밤 9시부터 오전 8시까지도 받아요",
            checked = night,
            onToggle = { onNightChange(!night) },
            // 야간 수신 동의는 광고성 동의가 선행돼야 한다(서버 규칙과 동일).
            enabled = !saving && marketing,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
    }
}

@Preview
@Composable
private fun NotificationSettingsPreview() {
    DesignSystemTheme {
        NotificationSettingsContent(
            enabled = true,
            marketing = true,
            night = false,
            saving = false,
            onCheckedChange = {},
            onMarketingChange = {},
            onNightChange = {},
            onBack = {},
        )
    }
}
