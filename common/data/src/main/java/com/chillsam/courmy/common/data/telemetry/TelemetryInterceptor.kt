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
                // 서버 로그에는 요청 자체가 남지 않는 구간이라, 앱에서만 알 수 있는 실패다.
                telemetry.recordFailure(
                    AppFailure(
                        area = AREA,
                        kind = "network_${e.javaClass.simpleName}",
                        detail = "${request.method} $path",
                        cause = e,
                    ),
                )
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
