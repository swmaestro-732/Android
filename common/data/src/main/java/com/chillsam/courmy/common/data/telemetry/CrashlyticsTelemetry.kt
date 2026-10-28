package com.chillsam.courmy.common.data.telemetry

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.chillsam.courmy.common.data.BuildConfig
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.FlowResult
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
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
 *
 * 제품 분석 지표(logFlow·setScreen)는 [AmplitudeClient] 로도 병행 전송한다 — 호출부(presentation)를
 * 바꾸지 않고 data 레이어에서만 갈라, 같은 이벤트가 Firebase 와 Amplitude 양쪽에 쌓인다.
 * [recordFailure] 는 크래시 리포트 전용이라 Crashlytics 에만 남긴다.
 */
@Singleton
class CrashlyticsTelemetry
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val amplitude: AmplitudeClient,
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

        // 메타(Facebook) 광고 전환용 로거. App ID 가 Manifest 에 없으면(=광고 설정 전) 생성이
        // 실패할 수 있어 null 로 두고 건너뛴다. 초기화는 SDK 가 앱 시작 시 자동으로 한다.
        private val metaLogger by lazy {
            runCatching { AppEventsLogger.newLogger(context) }.getOrNull()
        }

        override fun setScreen(name: String) {
            // custom key 는 마지막 값만 남아 "죽은 화면"이 되고, log 는 쌓여서 "거기까지 온 경로"가 된다.
            crashlytics?.setCustomKey(KEY_SCREEN, name)
            crashlytics?.log("screen: $name")
            amplitude.track(EVENT_SCREEN_VIEW, mapOf(KEY_SCREEN to name))
            debugLog("screen: $name")
        }

        override fun logFlow(
            flow: AppFlow,
            result: FlowResult,
            detail: String?,
        ) {
            val succeeded = result == FlowResult.Success
            val resultValue = if (succeeded) RESULT_SUCCESS else RESULT_FAILURE
            analytics?.logEvent(
                flow.eventName,
                Bundle().apply {
                    putString(PARAM_RESULT, resultValue)
                    detail?.let { putString(PARAM_DETAIL, it) }
                },
            )
            amplitude.track(
                eventName = flow.eventName,
                properties =
                    buildMap {
                        put(PARAM_RESULT, resultValue)
                        detail?.let { put(PARAM_DETAIL, it) }
                    },
            )
            // 광고 전환은 성공한 핵심 플로우만 Meta 표준 이벤트로 보낸다(실패는 전환이 아니다).
            if (succeeded) {
                metaEventName(flow)?.let { metaLogger?.logEvent(it) }
            }
            // 실패는 크래시 리포트에도 남겨야 "죽기 직전에 뭘 하다 실패했는지"가 보인다.
            val outcome = if (succeeded) "ok" else "fail"
            val suffix = detail?.let { " ($it)" }.orEmpty()
            crashlytics?.log("flow: ${flow.eventName} $outcome$suffix")
            debugLog("flow: ${flow.eventName} $outcome$suffix")
        }

        /**
         * 핵심 플로우를 Meta 표준 이벤트로 매핑한다. 광고 최적화에 쓰는 전환만 보내고,
         * 나머지(로그인·좋아요 등)는 Firebase·Amplitude 로만 남긴다(Meta 쪽 노이즈 방지).
         */
        private fun metaEventName(flow: AppFlow): String? =
            when (flow) {
                AppFlow.Signup -> AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION
                AppFlow.PlaceSave, AppFlow.CourseSave -> AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST
                AppFlow.CourseDetailLoad -> AppEventsConstants.EVENT_NAME_VIEWED_CONTENT
                else -> null
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
            const val EVENT_SCREEN_VIEW = "screen_view"
            const val KEY_FAILURE_AREA = "failure_area"
            const val KEY_FAILURE_KIND = "failure_kind"
            const val PARAM_RESULT = "result"
            const val PARAM_DETAIL = "detail"
            const val RESULT_SUCCESS = "success"
            const val RESULT_FAILURE = "failure"
        }
    }
