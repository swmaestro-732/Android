package com.chillsam.courmy.common.data.telemetry

import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.FlowResult
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import io.mockk.every
import io.mockk.mockk
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * 취소된 call 이 실패로 기록되지 않는지 검증한다.
 *
 * 화면을 벗어나면 코루틴이 취소되고 OkHttp 는 `IOException("Canceled")` 를 던지는데, 이걸 남기면
 * 목록을 스크롤하다 뒤로 가기만 해도 리포트가 쌓여 진짜 네트워크 장애가 묻힌다.
 */
class TelemetryInterceptorTest {
    private class RecordingTelemetry : Telemetry {
        val failures = mutableListOf<AppFailure>()

        override fun setScreen(name: String) = Unit

        override fun logFlow(
            flow: AppFlow,
            result: FlowResult,
            detail: String?,
        ) = Unit

        override fun recordFailure(failure: AppFailure) {
            failures += failure
        }
    }

    private fun runIntercept(
        canceled: Boolean,
        telemetry: Telemetry,
    ) {
        val request = Request.Builder().url("https://example.com/service/v1/courses/18").build()
        val call = mockk<Call>().also { every { it.isCanceled() } returns canceled }
        val chain =
            mockk<Interceptor.Chain>().also {
                every { it.request() } returns request
                every { it.call() } returns call
                every { it.proceed(any()) } throws IOException("Canceled")
            }

        runCatching { TelemetryInterceptor(telemetry).intercept(chain) }
    }

    @Test
    fun `취소된 call 은 실패로 기록하지 않는다`() {
        val telemetry = RecordingTelemetry()

        runIntercept(canceled = true, telemetry = telemetry)

        assertTrue(telemetry.failures.isEmpty())
    }

    @Test
    fun `취소가 아닌 네트워크 실패는 기록한다`() {
        val telemetry = RecordingTelemetry()

        runIntercept(canceled = false, telemetry = telemetry)

        assertEquals(1, telemetry.failures.size)
        // 경로의 식별자는 집계를 위해 {id} 로 정규화된다.
        assertEquals("GET /service/v1/courses/{id}", telemetry.failures.single().detail)
    }
}
