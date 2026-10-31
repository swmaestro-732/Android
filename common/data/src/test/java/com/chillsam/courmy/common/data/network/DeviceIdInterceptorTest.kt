package com.chillsam.courmy.common.data.network

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DeviceIdInterceptorTest {
    @get:Rule val directory = TemporaryFolder()

    private fun context(): Context = mockk<Context>().also { every { it.noBackupFilesDir } returns directory.root }

    private fun header(
        interceptor: DeviceIdInterceptor,
        url: String,
    ): String? {
        val request = Request.Builder().url(url).build()
        val sent = slot<Request>()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(capture(sent)) } answers {
            Response
                .Builder()
                .request(sent.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("".toResponseBody())
                .build()
        }
        interceptor.intercept(chain).close()
        return sent.captured.header("X-Device-Id")
    }

    @Test fun `같은 설치에서 재생성해도 ID는 유지되고 삭제하면 새 ID를 발급한다`() {
        val url = "https://$apiHost/api/v1/my/devices"
        val first = header(DeviceIdInterceptor(context()), url)
        assertEquals(first, header(DeviceIdInterceptor(context()), url))
        java.io.File(directory.root, "device-id").delete()
        assertNotEquals(first, header(DeviceIdInterceptor(context()), url))
    }

    @Test fun `외부 호스트와 평문 연결에는 ID를 전달하지 않는다`() {
        val interceptor = DeviceIdInterceptor(context())
        assertNull(header(interceptor, "https://example.com/api/v1/my/devices"))
        assertNull(header(interceptor, "http://$apiHost/api/v1/my/devices"))
    }
}
