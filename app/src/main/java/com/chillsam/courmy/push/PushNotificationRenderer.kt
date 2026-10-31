package com.chillsam.courmy.push

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.chillsam.courmy.R
import com.chillsam.courmy.common.domain.push.PushTarget
import com.chillsam.courmy.main.presentation.MainActivity
import com.google.firebase.messaging.RemoteMessage

/** FCM 서비스와 debug 미리보기가 같은 시스템 알림 레이아웃·탭 데이터를 사용한다. */
class PushNotificationRenderer(
    private val context: Context,
) {
    fun show(
        message: RemoteMessage,
        target: PushTarget,
        destination: Intent = Intent(context, MainActivity::class.java),
    ) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return
        val intent =
            destination.apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                // filterEquals에서 알림별 PendingIntent를 구분한다. 토큰은 URI에 넣지 않는다.
                action = "com.chillsam.courmy.PUSH.${target.notificationId}"
                listOf("type", "targetType", "targetId", "notificationId").forEach { putExtra(it, message.data[it]) }
            }
        val pending =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val notification =
            NotificationCompat
                .Builder(context, "courmy_activity")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(message.notification?.title ?: message.data["title"] ?: "Courmy")
                .setContentText(message.notification?.body ?: message.data["body"].orEmpty())
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        message.notification?.body ?: message.data["body"].orEmpty(),
                    ),
                ).setAutoCancel(true)
                .setContentIntent(pending)
                .build()
        manager.notify(target.notificationId.toString(), 0, notification)
    }
}
