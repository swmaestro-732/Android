package com.chillsam.courmy.common.data.telemetry

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.chillsam.courmy.common.data.BuildConfig
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.FlowResult
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Crashlytics·Analytics 기반 [Telemetry] 구현.
 *
 * debug 빌드에서는 두 SDK 의 수집이 매니페스트로 꺼져 있어(`app/src/debug/AndroidManifest.xml`)
 * 호출해도 전송되지 않는다. 그래서 별도 debug 구현을 두는 대신, 개발 중 확인용으로 logcat 에만 함께 남긴다.
 */
@Singleton
class CrashlyticsTelemetry
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : Telemetry {
        /**
         * `app/google-services.json` 은 커밋하지 않으므로(`.gitignore`), 파일 없이 클론한 개발자의
         * debug 빌드에는 `google_app_id` 리소스가 없어 기본 FirebaseApp 이 초기화되지 않는다.
         * 그 상태에서 `getInstance()` 는 `IllegalStateException` 을 던지는데, 이 구현은
         * [com.chillsam.courmy.common.data.telemetry.TelemetryInterceptor] 를 통해 **OkHttp 워커
         * 스레드에서도** 호출된다. 거기서 던지면 IOException 이 아니라 OkHttp 가 되던져 프로세스가
         * 죽고, 호출부의 runCatching 은 다른 스레드라 잡지 못한다.
         *
         * 계측은 없어도 앱은 돌아야 하므로 null 로 두고 조용히 건너뛴다. release 빌드는 Gradle 이
         * google-services.json 을 강제하므로(`app/build.gradle.kts`) 실제 출시본에서는 항상 붙는다.
         */
        private val crashlytics by lazy {
            runCatching { FirebaseCrashlytics.getInstance() }.getOrNull()
        }
        private val analytics by lazy {
            runCatching { FirebaseAnalytics.getInstance(context) }.getOrNull()
        }

        override fun setScreen(name: String) {
            // custom key 는 마지막 값만 남아 "죽은 화면"이 되고, log 는 쌓여서 "거기까지 온 경로"가 된다.
            crashlytics?.setCustomKey(KEY_SCREEN, name)
            crashlytics?.log("screen: $name")
            debugLog("screen: $name")
        }

        override fun logFlow(
            flow: AppFlow,
            result: FlowResult,
            detail: String?,
        ) {
            val succeeded = result == FlowResult.Success
            analytics?.logEvent(
                flow.eventName,
                Bundle().apply {
                    putString(PARAM_RESULT, if (succeeded) RESULT_SUCCESS else RESULT_FAILURE)
                    detail?.let { putString(PARAM_DETAIL, it) }
                },
            )
            // 실패는 크래시 리포트에도 남겨야 "죽기 직전에 뭘 하다 실패했는지"가 보인다.
            val outcome = if (succeeded) "ok" else "fail"
            val suffix = detail?.let { " ($it)" }.orEmpty()
            crashlytics?.log("flow: ${flow.eventName} $outcome$suffix")
            debugLog("flow: ${flow.eventName} $outcome$suffix")
        }

        override fun recordFailure(failure: AppFailure) {
            crashlytics?.setCustomKey(KEY_FAILURE_AREA, failure.area)
            crashlytics?.setCustomKey(KEY_FAILURE_KIND, failure.kind)
            // cause 를 그대로 넘기면 메시지에 서버 응답 본문이 섞여 들어올 수 있어, 요약 예외로 감싸 올린다.
            // 원인 스택은 cause 로 붙여 두면 리포트에서 함께 볼 수 있다.
            val summary = "${failure.area}/${failure.kind}${failure.detail?.let { " $it" }.orEmpty()}"
            crashlytics?.recordException(TelemetryFailure(summary, failure.cause))
            debugLog("failure: $summary", failure.cause)
        }

        private fun debugLog(
            message: String,
            cause: Throwable? = null,
        ) {
            if (!BuildConfig.DEBUG) return
            if (cause == null) Log.d(TAG, message) else Log.w(TAG, message, cause)
        }

        /** 리포트 제목을 `area/kind` 로 묶기 위한 예외 타입. 원인 예외는 [cause] 로 붙는다. */
        private class TelemetryFailure(
            message: String,
            cause: Throwable?,
        ) : Exception(message, cause)

        private companion object {
            const val TAG = "Telemetry"
            const val KEY_SCREEN = "screen"
            const val KEY_FAILURE_AREA = "failure_area"
            const val KEY_FAILURE_KIND = "failure_kind"
            const val PARAM_RESULT = "result"
            const val PARAM_DETAIL = "detail"
            const val RESULT_SUCCESS = "success"
            const val RESULT_FAILURE = "failure"
        }
    }
