package com.chillsam.courmy.common.data.push

import android.content.Context
import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.data.network.appBuildNumber
import com.chillsam.courmy.common.domain.push.NotificationSettings
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

/** 등록과 로그아웃 해제를 직렬화해 늦게 끝난 PUT이 로그아웃한 사용자를 다시 등록하지 않게 한다. */
@Singleton
class DeviceRegistration
    @Inject
    constructor(
        private val api: DeviceApi,
        private val tokens: TokenStore,
        private val fcm: FcmTokenClient,
        private val telemetry: Telemetry,
        private val preferences: PushPreferences,
        @ApplicationContext private val context: Context,
    ) : BaseRemoteDataSource(),
        NotificationSettings {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val mutex = Mutex()
        private val settingsMutex = Mutex()

        override val notificationsEnabled: Boolean
            get() = preferences.enabled

        override suspend fun setNotificationsEnabled(enabled: Boolean) =
            withContext(Dispatchers.IO) {
                settingsMutex.withLock {
                    if (enabled) {
                        preferences.enabled = true
                        onSessionAvailable()
                    } else {
                        // 먼저 저장해 토큰 갱신/재로그인이 OFF를 되돌리지 못하게 한다.
                        preferences.enabled = false
                        unregister(requireTokenDeletion = true)
                    }
                }
            }

        @Volatile private var enabled = true

        @Volatile private var generation = 0L

        fun onSessionAvailable() {
            enabled = tokens.isLoggedIn
            sync()
        }

        fun sync() {
            val expectedGeneration = generation
            scope.launch {
                mutex.withLock {
                    if (!preferences.enabled) {
                        safely("disable_auto_init") { fcm.disableAutoInit() }
                        safely("delete_token") { fcm.delete() }
                        return@withLock
                    }
                    if (!tokens.isLoaded) tokens.load()
                    val session = tokens.userId ?: return@withLock
                    if (!enabled || generation != expectedGeneration) return@withLock
                    safely("register") {
                        fcm.enableAutoInit()
                        val token = fcm.token()
                        if (!enabled || !preferences.enabled) return@safely
                        if (generation != expectedGeneration || tokens.userId != session) return@safely
                        val build = requireNotNull(context.appBuildNumber())
                        require(
                            checkResponse(
                                api.register(
                                    RegisterDeviceRequest(token = token, platform = "ANDROID", appBuild = build),
                                ),
                            ).code ==
                                2000,
                        )
                    }
                }
            }
        }

        /** 인증을 지우기 전에 호출한다. 실패해도 로컬 FCM 토큰을 폐기하고 로그아웃은 진행한다. */
        suspend fun unregister(
            removeRemote: Boolean = true,
            requireTokenDeletion: Boolean = false,
        ) = withContext(NonCancellable) {
            enabled = false
            generation++
            safely("disable_auto_init") { fcm.disableAutoInit() }
            mutex.withLock {
                try {
                    if (removeRemote) {
                        safely("unregister") {
                            val token = fcm.token()
                            require(checkResponse(api.remove(RemoveDeviceRequest(token))).code == 2000)
                        }
                    }
                } finally {
                    try {
                        if (requireTokenDeletion) {
                            withTimeout(OPERATION_TIMEOUT_MS) { fcm.delete() }
                        } else {
                            safely("delete_token") { fcm.delete() }
                        }
                    } finally {
                        context.getSystemService(android.app.NotificationManager::class.java).cancelAll()
                    }
                }
            }
        }

        private suspend fun safely(
            action: String,
            block: suspend () -> Unit,
        ) {
            runCatching {
                withTimeout(OPERATION_TIMEOUT_MS) { block() }
            }.onFailure { failure ->
                if (failure is Error) throw failure
                if (failure is CancellationException && failure !is kotlinx.coroutines.TimeoutCancellationException) {
                    throw failure
                }
                // 예외 메시지/본문은 토큰을 포함할 수 있어 클래스 이름만 분류값으로 남긴다.
                telemetry.recordFailure(AppFailure("push", failure.javaClass.simpleName, action))
            }
        }

        private companion object {
            const val OPERATION_TIMEOUT_MS = 10_000L
        }
    }
