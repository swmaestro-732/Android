package com.chillsam.courmy.common.data.network

import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 426 Upgrade Required 를 잡아 [AppUpdateEventBus] 로 알린다.
 *
 * DataSource 마다 처리하지 않고 인터셉터에 둔다 — 강제 업데이트는 어떤 화면의 어떤 요청에서
 * 내려와도 똑같이 앱 전체를 막아야 하고, 응답을 그대로 흘려보내므로 각 화면의 기존 에러 처리
 * ([com.chillsam.courmy.common.domain.error.HttpResponseException]) 는 건드리지 않는다.
 *
 * [apiHost] 로 우리 API 응답만 본다. 카카오·네이버·S3 프리사인 같은 서드파티가 자기 사정으로 426 을
 * 내려도 그건 "우리 앱이 낡았다"는 뜻이 아니다.
 */
class UpdateRequiredInterceptor(
    private val json: Json,
    private val apiHost: String?,
    private val appUpdateEventBus: AppUpdateEventBus,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == UPGRADE_REQUIRED && response.request.url.host == apiHost) {
            appUpdateEventBus.notifyUpdateRequired(response.readServerMessage())
        }
        return response
    }

    /**
     * 공통 에러 봉투(`{ "code": ..., "message": ... }`)의 `message` 만 꺼낸다.
     *
     * `peekBody` 라 본문을 소비하지 않는다 — 뒤따르는 로깅·에러 파싱이 같은 본문을 다시 읽는다.
     * 문구는 부가 정보라 파싱이 어긋나도 삼키고 화면 기본 문구로 넘어간다.
     */
    private fun Response.readServerMessage(): String? =
        runCatching {
            val body = peekBody(BODY_PEEK_BYTES).string()
            json
                .parseToJsonElement(body)
                .jsonObject[FIELD_MESSAGE]
                ?.jsonPrimitive
                ?.contentOrNull
        }.getOrNull()

    private companion object {
        /** RFC 7231 426 Upgrade Required. 서버가 정한 최소 지원 버전보다 앱이 낮다. */
        const val UPGRADE_REQUIRED = 426
        const val FIELD_MESSAGE = "message"

        /** 안내 문구 한 줄만 필요하므로 본문 앞부분만 읽는다. */
        const val BODY_PEEK_BYTES = 8L * 1024
    }
}
