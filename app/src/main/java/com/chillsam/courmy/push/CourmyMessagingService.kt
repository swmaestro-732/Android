package com.chillsam.courmy.push

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.data.push.DeviceRegistration
import com.chillsam.courmy.common.domain.push.NotificationEvents
import com.chillsam.courmy.common.domain.push.PushTarget
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@AndroidEntryPoint
class CourmyMessagingService : FirebaseMessagingService() {
    @Inject lateinit var devices: DeviceRegistration

    @Inject
    lateinit var tokens: TokenStore

    @Inject
    lateinit var notificationEvents: NotificationEvents

    override fun onNewToken(token: String) {
        // 원문을 저장하거나 로깅하지 않고 SDK의 최신 토큰을 다시 조회한다.
        devices.sync()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val target = PushTarget.parse(message.data) ?: return
        // FCM은 worker thread에서 호출한다. 데이터 메시지도 로그아웃 후에는 표시하지 않는다.
        val loggedIn =
            runBlocking {
                withTimeoutOrNull(SESSION_TIMEOUT_MS) {
                    tokens.load()
                    tokens.isLoggedIn
                }
            }
        if (loggedIn != true) return
        notificationEvents.notifyChanged()
        PushNotificationRenderer(this).show(message, target)
    }

    private companion object {
        const val SESSION_TIMEOUT_MS = 3_000L
    }
}
