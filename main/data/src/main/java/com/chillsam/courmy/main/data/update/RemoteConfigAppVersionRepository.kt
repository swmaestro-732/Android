package com.chillsam.courmy.main.data.update

import android.content.Context
import android.os.Build
import com.chillsam.courmy.main.domain.update.AppVersionRepository
import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RemoteConfigAppVersionRepository internal constructor(
    private val installedVersionCode: () -> Long,
    private val fetchLatestVersion: suspend (Boolean) -> Pair<Long, String>,
) : AppVersionRepository {
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : this(
        installedVersionCode = {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                info.versionCode.toLong()
            }
        },
        fetchLatestVersion = { forceRefresh ->
            // Firebase가 미설정된 빌드도 조회 실패로만 처리하도록 호출 시점에 초기화한다.
            val config = FirebaseRemoteConfig.getInstance()
            config.setDefaultsAsync(mapOf(LATEST_VERSION_CODE to 0L, LATEST_VERSION_NAME to "")).awaitResult()
            config.fetch(if (forceRefresh) 0L else FETCH_INTERVAL_SECONDS).awaitResult()
            config.activate().awaitResult()
            config.getLong(LATEST_VERSION_CODE) to config.getString(LATEST_VERSION_NAME).trim()
        },
    )

    override suspend fun checkVersion(forceRefresh: Boolean): AppVersionCheckVO =
        try {
            withTimeoutOrNull(CHECK_TIMEOUT_MS) {
                val installed = installedVersionCode()
                val (latest, name) = fetchLatestVersion(forceRefresh)
                if (installed > 0 && latest > 0) {
                    AppVersionCheckVO.Checked(installed, latest, name)
                } else {
                    AppVersionCheckVO.Unavailable
                }
            } ?: AppVersionCheckVO.Unavailable
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            AppVersionCheckVO.Unavailable
        }

    private companion object {
        const val LATEST_VERSION_CODE = "latest_version_code"
        const val LATEST_VERSION_NAME = "latest_version_name"
        const val FETCH_INTERVAL_SECONDS = 3_600L
        const val CHECK_TIMEOUT_MS = 5_000L
    }
}

private suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { value -> if (continuation.isActive) continuation.resume(value) }
        addOnFailureListener { error -> if (continuation.isActive) continuation.resumeWithException(error) }
        addOnCanceledListener {
            if (continuation.isActive) {
                continuation.resumeWithException(IllegalStateException("Remote Config task cancelled"))
            }
        }
    }
