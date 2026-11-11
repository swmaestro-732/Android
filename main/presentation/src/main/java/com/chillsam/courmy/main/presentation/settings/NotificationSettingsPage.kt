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
import androidx.compose.ui.res.stringResource
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
import com.chillsam.courmy.main.presentation.R
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.notification.NotificationPermissionPrimingDialog
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
    // OS 권한 프롬프트 직전에 띄우는 Courmy 사전 안내. "허용"을 눌러야 실제 시스템 프롬프트로 넘어간다.
    var showPermissionPriming by rememberSaveable { mutableStateOf(false) }
    val consentCaptionFormat = stringResource(R.string.main_notification_settings_consent_caption)
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
                            // 시스템 프롬프트 전에 Courmy 사전 안내부터. "허용" 에서만 프롬프트로 넘어간다.
                            showPermissionPriming = true
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
                caption = consentCaption(consentCaptionFormat, state.marketing, state.marketingAgreedAt),
            ),
        night =
            NotificationToggleState(
                checked = state.night,
                onToggle = { viewModel.onIntent(NotificationSettingsIntent.SetNight(it)) },
                caption = consentCaption(consentCaptionFormat, state.night, state.nightAgreedAt),
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
    if (showPermissionPriming) {
        NotificationPermissionPrimingDialog(
            onAllow = {
                showPermissionPriming = false
                // 시스템 프롬프트는 설치당 1회뿐이라 실제로 띄우는 이 시점에만 "요청함"으로 기록한다.
                permissionPreferences.edit().putBoolean("requested", true).apply()
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onDismiss = { showPermissionPriming = false },
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
        title =
            if (error) {
                stringResource(R.string.main_notification_settings_dialog_error_title)
            } else {
                stringResource(R.string.main_notification_settings_dialog_grant_title)
            },
        description =
            if (error) {
                stringResource(R.string.main_notification_settings_dialog_error_description)
            } else {
                stringResource(R.string.main_notification_settings_dialog_grant_description)
            },
        confirmText = stringResource(R.string.main_notification_settings_dialog_confirm),
        dismissText = stringResource(R.string.main_notification_settings_dialog_dismiss),
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
    captionFormat: String,
    agreed: Boolean,
    agreedAt: String?,
): String? {
    if (!agreed || agreedAt == null) return null
    val instant = runCatching { Instant.parse(agreedAt) }.getOrNull() ?: return null
    return captionFormat.format(consentDateFormatter.format(instant))
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
        BackTopBar(title = stringResource(R.string.main_notification_settings_title), onBack = onBack, boxed = true)
        // 홈 최초 진입 동의 팝업과 같은 항목·문구를 쓴다. 여기서는 모두 켜고 끌 수 있다.
        NotificationToggleRow(
            title = stringResource(R.string.main_notification_settings_activity_title),
            description = stringResource(R.string.main_notification_settings_activity_description),
            checked = activity.checked,
            onToggle = { activity.onToggle(!activity.checked) },
            enabled = !saving,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
        NotificationToggleRow(
            title = stringResource(R.string.main_notification_settings_marketing_title),
            description = stringResource(R.string.main_notification_settings_marketing_description),
            checked = marketing.checked,
            onToggle = { marketing.onToggle(!marketing.checked) },
            caption = marketing.caption,
            enabled = !saving,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        SettingsDivider()
        // 야간을 먼저 켜면 광고 동의가 함께 켜진다 — 토글을 흐리게 두지 않고 모양을 통일한다.
        NotificationToggleRow(
            title = stringResource(R.string.main_notification_settings_night_title),
            description = stringResource(R.string.main_notification_settings_night_description),
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
