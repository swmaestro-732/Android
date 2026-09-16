package com.chillsam.courmy.common.data.network

import com.chillsam.courmy.common.data.appUpdate.AppUpdateEventBusImpl
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 426 만 골라 강제 업데이트로 올리는지, 그러면서 응답 본문을 망가뜨리지 않는지 검증한다.
 *
 * 본문을 소비해 버리면 뒤따르는 로깅·에러 파싱이 빈 본문을 읽게 되는데, 인터셉터 하나가
 * 그런 식으로 조용히 망가뜨리면 원인 찾기가 아주 어렵다.
 */
class UpdateRequiredInterceptorTest {
    private val apiHost = "api.courmy.com"

    private fun intercept(
        url: String,
        code: Int,
        body: String = "",
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
        return eventBus to UpdateRequiredInterceptor(apiHost, eventBus).intercept(chain)
    }

    @Test
    fun `우리 API 의 426 이면 강제 업데이트를 알린다`() {
        val (eventBus, _) =
            intercept(
                url = "https://$apiHost/service/v1/courses/18",
                code = 426,
                body = """{"code":4260,"message":"앱 업데이트가 필요합니다."}""",
            )

        assertTrue(eventBus.updateRequired.value)
    }

    @Test
    fun `426 응답 본문은 소비되지 않아 뒤에서 다시 읽을 수 있다`() {
        val body = """{"code":4260,"message":"앱 업데이트가 필요합니다."}"""
        val (_, response) = intercept("https://$apiHost/service/v1/courses/18", 426, body)

        assertEquals(body, response.body.string())
    }

    @Test
    fun `본문이 봉투 형식이 아니어도 강제 업데이트는 걸린다`() {
        // 상태 코드만 보고 판단한다 — 봉투 스키마가 바뀌어도 조용히 안 걸리는 쪽으로 깨지면 안 된다.
        val (eventBus, _) = intercept("https://$apiHost/service/v1/courses/18", 426, "not json")

        assertTrue(eventBus.updateRequired.value)
    }

    @Test
    fun `서드파티 호스트의 426 은 무시한다`() {
        val (eventBus, _) =
            intercept(
                url = "https://dapi.kakao.com/v2/search/image",
                code = 426,
                body = """{"message":"upgrade"}""",
            )

        assertFalse(eventBus.updateRequired.value)
    }

    @Test
    fun `426 이 아닌 실패는 그대로 흘려보낸다`() {
        val (eventBus, _) =
            intercept(
                url = "https://$apiHost/service/v1/courses/18",
                code = 404,
                body = """{"code":4040,"message":"없는 코스예요."}""",
            )

        assertFalse(eventBus.updateRequired.value)
    }

    @Test
    fun `헤더 형식 오류로 내려오는 400 은 강제 업데이트가 아니다`() {
        // INVALID_APP_HEADER(4005). 우리 잘못이지 사용자 앱이 낡았다는 뜻이 아니다.
        val (eventBus, _) =
            intercept(
                url = "https://$apiHost/service/v1/home",
                code = 400,
                body = """{"code":4005,"message":"앱 버전 헤더가 올바르지 않습니다."}""",
            )

        assertFalse(eventBus.updateRequired.value)
    }
}
