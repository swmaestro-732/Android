package com.chillsam.courmy.main.presentation.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.chillsam.courmy.main.presentation.R as MainR

/**
 * OS 알림 권한 상태 판정과 기기 설정 이동. 알림 동의 팝업과 설정 > 알림이 같은 규칙을 쓴다.
 */
internal fun Context.openNotificationSettings(): Boolean {
    val intent =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        }
    return runCatching { startActivity(intent) }
        .onFailure {
            Toast
                .makeText(
                    this,
                    getString(MainR.string.main_notification_settings_open_failed),
                    Toast.LENGTH_SHORT,
                ).show()
        }.isSuccess
}

internal fun NotificationManagerCompat.canRequestNotificationPermission(requested: Boolean): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !areNotificationsEnabled() && !requested

internal fun NotificationManagerCompat.canShowActivityNotifications(): Boolean =
    areNotificationsEnabled() &&
        (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                getNotificationChannel("courmy_activity")?.importance != NotificationManager.IMPORTANCE_NONE
        )
