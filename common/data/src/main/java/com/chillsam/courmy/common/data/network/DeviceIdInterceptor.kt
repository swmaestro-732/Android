package com.chillsam.courmy.common.data.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** 사용자 ID는 JWT가 식별한다. 이 값은 계정 전환에도 유지되는 앱 설치 식별자다. */
@Singleton
class DeviceIdInterceptor
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : Interceptor {
        // 기기 복원 시 다른 설치에 같은 ID가 복사되지 않도록 백업 제외 디렉터리에 저장한다.
        private val deviceId: String by lazy {
            val file = File(context.noBackupFilesDir, "device-id")
            file.takeIf { it.exists() }?.readText()?.trim()?.takeIf { value ->
                runCatching { UUID.fromString(value) }.isSuccess
            } ?: UUID.randomUUID().toString().also { file.writeText(it) }
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            return chain.proceed(
                if (request.url.host == apiHost && request.url.isHttps) {
                    request.newBuilder().header("X-Device-Id", deviceId).build()
                } else {
                    request
                },
            )
        }
    }
