package com.chillsam.courmy.common.data.network

import com.chillsam.courmy.common.data.appUpdate.AppUpdateEventBusImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 426 만 골라 강제 업데이트로 올리는지, 그러면서 응답 본문을 망가뜨리지 않는지 검증한다.
 *
 * 본문을 소비해 버리면 뒤따르는 로깅·에러 파싱이 빈 본문을 읽게 되는데, 인터셉터 하나가
 * 그런 식으로 조용히 망가뜨리면 원인 찾기가 아주 어렵다.
 */
class UpdateRequiredInterceptorTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val apiHost = "api.courmy.com"

    private fun intercept(
        url: String,
        code: Int,
        body: String,
    ): Pair<AppUpdateEventBusImpl, Response> {
        val eventBus = AppUpdateEventBusImpl()
        val request = Request.Builder().url(url).build()
        val response =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message("")
                .body(body.toResponseBody("application/json".toMediaType()))
                .build()
        val chain =
            mockk<Interceptor.Chain>().also {
                every { it.request() } returns request
                every { it.proceed(any()) } returns response
            }
        return eventBus to UpdateRequiredInterceptor(json, apiHost, eventBus).intercept(chain)
    }

    @Test
    fun `426 이면 서버 문구와 함께 강제 업데이트를 알린다`() {
        val (eventBus, _) =
            intercept(
                url = "https://$apiHost/service/v1/courses/18",
                code = 426,
                body = """{"code":4260,"message":"9월 30일부터 이전 버전은 지원되지 않아요."}""",
            )

        assertEquals("9월 30일부터 이전 버전은 지원되지 않아요.", eventBus.updateRequired.value?.message)
    }

    @Test
    fun `426 응답 본문은 소비되지 않아 뒤에서 다시 읽을 수 있다`() {
        val body = """{"code":4260,"message":"업데이트가 필요해요."}"""
        val (_, response) = intercept("https://$apiHost/service/v1/courses/18", 426, body)

        assertEquals(body, response.body.string())
    }

    @Test
    fun `문구를 못 읽어도 강제 업데이트 자체는 알린다`() {
        val (eventBus, _) = intercept("https://$apiHost/service/v1/courses/18", 426, "not json")

        // 상태는 켜지되 문구만 비어 화면 기본 문구로 넘어간다.
        assertEquals(true, eventBus.updateRequired.value != null)
        assertNull(eventBus.updateRequired.value?.message)
    }

    @Test
    fun `서드파티 호스트의 426 은 무시한다`() {
        val (eventBus, _) =
            intercept(
                url = "https://dapi.kakao.com/v2/search/image",
                code = 426,
                body = """{"message":"upgrade"}""",
            )

        assertNull(eventBus.updateRequired.value)
    }

    @Test
    fun `426 이 아닌 실패는 그대로 흘려보낸다`() {
        val (eventBus, _) =
            intercept(
                url = "https://$apiHost/service/v1/courses/18",
                code = 404,
                body = """{"code":4040,"message":"없는 코스예요."}""",
            )

        assertNull(eventBus.updateRequired.value)
    }
}
