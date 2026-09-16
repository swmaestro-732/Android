package com.chillsam.courmy.common.data.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 서버가 버전을 판정하는 근거가 이 헤더뿐이라, 잘못 붙으면 400(`INVALID_APP_HEADER`)으로 앱 전체가
 * 죽고 안 붙으면 낡은 앱이 조용히 통과한다. 어디에 붙고 어디에 안 붙는지를 못 박아 둔다.
 */
class AppVersionInterceptorTest {
    private val apiHost = "api.courmy.com"

    /** 인터셉터가 실제로 내보낸 요청을 돌려준다(헤더는 chain.proceed 로 넘어간 쪽에 붙는다). */
    private fun proceededRequest(
        url: String,
        appBuild: Long?,
    ): Request {
        val original = Request.Builder().url(url).build()
        val sent = slot<Request>()
        val chain =
            mockk<Interceptor.Chain>().also {
                every { it.request() } returns original
                every { it.proceed(capture(sent)) } answers {
                    Response
                        .Builder()
                        .request(sent.captured)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("")
                        .body("".toResponseBody())
                        .build()
                }
            }
        AppVersionInterceptor(apiHost, appBuild).intercept(chain)
        return sent.captured
    }

    @Test
    fun `우리 API 요청에는 빌드 번호와 플랫폼을 붙인다`() {
        val request = proceededRequest("https://$apiHost/service/v1/home", appBuild = 4)

        assertEquals("4", request.header("X-App-Build"))
        assertEquals("Android", request.header("X-App-Platform"))
    }

    @Test
    fun `서드파티 호스트에는 앱 버전을 붙이지 않는다`() {
        val request = proceededRequest("https://dapi.kakao.com/v2/search/image", appBuild = 4)

        assertNull(request.header("X-App-Build"))
        assertNull(request.header("X-App-Platform"))
    }

    @Test
    fun `빌드 번호를 못 구하면 두 헤더를 모두 생략한다`() {
        // 계약상 헤더가 없으면 정상 통과다. 가짜 값으로 채우면 400 으로 전부 죽는다.
        val request = proceededRequest("https://$apiHost/service/v1/home", appBuild = null)

        assertNull(request.header("X-App-Build"))
        assertNull(request.header("X-App-Platform"))
    }

    @Test
    fun `헤더 값은 접미사 없는 정수 문자열이다`() {
        // "4 (0.1.2)" 같은 사람이 읽기 좋은 형식으로 흘리면 서버 파싱이 깨진다.
        val build = proceededRequest("https://$apiHost/service/v1/home", appBuild = 4).header("X-App-Build")

        assertEquals(4L, build?.toLongOrNull())
    }
}
