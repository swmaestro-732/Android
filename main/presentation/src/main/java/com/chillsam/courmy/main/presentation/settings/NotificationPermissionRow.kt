package com.chillsam.courmy.main.presentation.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume

/** OS 권한 상태만 표시한다. 서버의 활동별 수신 설정과는 별개다. */
@Composable
internal fun NotificationPermissionRow() {
    val context = LocalContext.current
    val notifications = remember(context) { NotificationManagerCompat.from(context) }
    val preferences = remember(context) { context.getSharedPreferences("notification_permission", 0) }
    var enabled by remember { mutableStateOf(notifications.areNotificationsEnabled()) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            enabled = notifications.areNotificationsEnabled()
            if (!enabled) {
                Toast.makeText(context, "알림을 받으려면 기기 설정에서 알림을 허용해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    RefreshOnResume { enabled = notifications.areNotificationsEnabled() }

    SettingsMenuRow(
        label = "기기 알림",
        hint = if (enabled) "허용됨" else "꺼짐",
        onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !enabled && !preferences.getBoolean("requested", false)
            ) {
                preferences.edit().putBoolean("requested", true).apply()
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                val intent =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    } else {
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}"),
                        )
                    }
                runCatching { context.startActivity(intent) }
                    .onFailure { Toast.makeText(context, "기기 알림 설정을 열 수 없어요.", Toast.LENGTH_SHORT).show() }
            }
        },
    )
}
