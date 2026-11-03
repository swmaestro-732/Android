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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        activity =
            NotificationToggleState(
                checked = state.enabled && permissionGranted,
                onToggle = { enabled ->
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
            ),
        marketing =
            NotificationToggleState(
                checked = state.marketing,
                onToggle = { viewModel.onIntent(NotificationSettingsIntent.SetMarketing(it)) },
                // 동의 중인 항목에만 서버가 확인한 처리 시각을 안내한다.
                caption = consentCaption(state.marketing, state.marketingAgreedAt),
            ),
        night =
            NotificationToggleState(
                checked = state.night,
                onToggle = { viewModel.onIntent(NotificationSettingsIntent.SetNight(it)) },
                caption = consentCaption(state.night, state.nightAgreedAt),
            ),
        saving = state.saving,
        onBack = navigation::navigateToBack,
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

/**
 * 설정 > 알림의 토글 한 행의 상태. (checked + 토글 콜백을 한 묶음으로 전달해 파라미터 수를 줄인다.)
 * [caption] 은 설명 아래 보조 줄로, 서버가 확인한 동의 처리 시각 안내 등에 쓴다(없으면 표시 안 함).
 */
internal data class NotificationToggleState(
    val checked: Boolean,
    val onToggle: (Boolean) -> Unit,
    val caption: String? = null,
)

private val consentDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA).withZone(ZoneId.systemDefault())

/**
 * 서버가 확인한 처리 시각을 "YYYY.MM.DD 동의" 안내 문구로 바꾼다(SCRUM-756).
 * 해당 항목이 꺼져 있거나([agreed] false) 시각이 없으면 안내하지 않는다.
 */
private fun consentCaption(
    agreed: Boolean,
    agreedAt: String?,
): String? {
    if (!agreed || agreedAt == null) return null
    val instant = runCatching { Instant.parse(agreedAt) }.getOrNull() ?: return null
    return "${consentDateFormatter.format(instant)} 동의"
}

@Composable
internal fun NotificationSettingsContent(
    activity: NotificationToggleState,
    marketing: NotificationToggleState,
    night: NotificationToggleState,
    saving: Boolean,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(Modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "알림", onBack = onBack, boxed = true)
        // 홈 최초 진입 동의 팝업과 같은 항목·문구를 쓴다. 여기서는 모두 켜고 끌 수 있다.
        NotificationToggleRow(
            title = "활동 알림",
            description = "내 코스의 좋아요와 댓글 소식을 받아요",
            checked = activity.checked,
            onToggle = { activity.onToggle(!activity.checked) },
            enabled = !saving,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
        NotificationToggleRow(
            title = "광고 알림",
            description = "추천 코스와 이벤트 소식을 받아요",
            checked = marketing.checked,
            onToggle = { marketing.onToggle(!marketing.checked) },
            caption = marketing.caption,
            enabled = !saving,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
        // 야간을 먼저 켜면 광고 동의가 함께 켜진다 — 토글을 흐리게 두지 않고 모양을 통일한다.
        NotificationToggleRow(
            title = "야간에도 받기",
            description = "밤 9시부터 오전 8시까지도 받아요",
            checked = night.checked,
            onToggle = { night.onToggle(!night.checked) },
            caption = night.caption,
            enabled = !saving,
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
            activity = NotificationToggleState(checked = true, onToggle = {}),
            marketing = NotificationToggleState(checked = true, onToggle = {}),
            night = NotificationToggleState(checked = false, onToggle = {}),
            saving = false,
            onBack = {},
        )
    }
}
