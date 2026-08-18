package com.chillsam.courmy.common.data.telemetry

import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * API 실패를 운영에서 볼 수 있게 남기는 인터셉터. **릴리스에서도 동작한다**
 * (debug 전용인 [com.chillsam.courmy.common.data.logging.ApiLogInterceptor] 와 역할이 다르다).
 *
 * 남기는 것은 "무엇이 어디서 실패했는지"뿐이다. 요청·응답 본문과 헤더는 건드리지 않으므로
 * 토큰·개인정보가 외부 리포트로 나가지 않는다.
 *
 * 기록하지 않는 것:
 * - 2xx·3xx — 정상.
 * - 400/401/403/404/409 — 입력 오류·토큰 만료·중복처럼 **앱이 정상적으로 처리하는 분기**다.
 *   이걸 남기면 리포트가 잡음으로 덮여 진짜 장애를 못 알아본다.
 * - 취소된 call — 화면 이탈로 코루틴이 취소된 것이라 장애가 아니다.
 */
class TelemetryInterceptor(
    private val telemetry: Telemetry,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = normalizePath(request.url.encodedPath)

        val response =
            try {
                chain.proceed(request)
            } catch (e: IOException) {
                // 화면을 벗어나 코루틴이 취소되면 OkHttp 가 진행 중인 call 을 끊고
                // IOException("Canceled") 를 던진다. 이건 장애가 아니라 정상적인 중단이다.
                // 목록을 스크롤하다 뒤로 가기만 해도 쌓이므로, 남기면 리포트가 취소로 덮여
                // 진짜 네트워크 장애(타임아웃·DNS 실패)를 못 알아본다.
                // TelemetryTracking.track 이 CancellationException 을 제외하는 것과 같은 이유다.
                if (!chain.call().isCanceled()) {
                    // 서버 로그에는 요청 자체가 남지 않는 구간이라, 앱에서만 알 수 있는 실패다.
                    telemetry.recordFailure(
                        AppFailure(
                            area = AREA,
                            kind = "network_${e.javaClass.simpleName}",
                            detail = "${request.method} $path",
                            cause = e,
                        ),
                    )
                }
                throw e
            }

        if (response.code >= HTTP_SERVER_ERROR) {
            // 서버 5xx 자체는 백엔드 로그에도 남지만, 어느 앱 버전·화면에서 터졌는지는 앱만 안다.
            telemetry.recordFailure(
                AppFailure(
                    area = AREA,
                    kind = "http_${response.code}",
                    detail = "${request.method} $path",
                ),
            )
        }
        return response
    }

    /**
     * 경로의 식별자 구간을 `{id}` 로 바꾼다.
     *
     * `/service/v1/courses/18` 을 그대로 남기면 리포트가 코스마다 다른 항목으로 쪼개져 집계가 안 되고,
     * 경로에 실린 식별자가 외부 리포트로 나간다.
     */
    private fun normalizePath(path: String): String =
        path
            .split("/")
            .joinToString("/") { segment ->
                if (segment.isNotEmpty() && segment.all(Char::isDigit)) "{id}" else segment
            }

    private companion object {
        const val AREA = "network"
        const val HTTP_SERVER_ERROR = 500
    }
}
