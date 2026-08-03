package com.chillsam.courmy.common.data.logging

import android.util.Log
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException

/**
 * API request/response 를 사람이 읽기 좋게 남기는 debug 전용 인터셉터.
 *
 * - 전용 태그 [TAG] 로 남기므로 `adb logcat -s API` 로 딱 필터된다.
 * - request/response body 를 JSON pretty-print 하고, 토큰 등 [SENSITIVE] 값은 자동으로 `***` 마스킹한다.
 * - [enabled] 가 false(=release) 면 아무것도 남기지 않고 그대로 통과시킨다(토큰 유출 방지).
 *
 * 헤더(Authorization 등)는 의도적으로 남기지 않는다. 필요한 값은 전부 body 로 확인한다.
 */
class ApiLogInterceptor(
    private val json: Json,
    private val enabled: Boolean,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!enabled) return chain.proceed(request)

        // 쿼리는 식별자(userId 등)가 평문으로 남을 수 있어 경로만 남긴다.
        val path = request.url.encodedPath
        Log.d(TAG, "⇢ ${request.method} $path")
        request.body?.let { body ->
            Log.d(TAG, "  req: ${pretty(readRequestBody(body))}")
        }

        val startNs = System.nanoTime()
        val response =
            try {
                chain.proceed(request)
            } catch (e: IOException) {
                Log.w(TAG, "⇠ FAIL ${request.method} $path: ${e.javaClass.simpleName} - ${e.message}")
                throw e
            }
        val tookMs = (System.nanoTime() - startNs) / 1_000_000

        // peekBody 는 응답 스트림을 소비하지 않고 복사본만 읽으므로, 이후 Retrofit 파싱에 영향이 없다.
        val bodyText = runCatching { response.peekBody(MAX_BODY_BYTES).string() }.getOrNull()
        Log.d(TAG, "⇠ ${response.code} $path (${tookMs}ms)")
        if (!bodyText.isNullOrBlank()) Log.d(TAG, "  res: ${pretty(bodyText)}")
        return response
    }

    /**
     * 요청 바디를 로그용으로 읽는다. one-shot/duplex 바디는 한 번만 소비 가능하므로 미리 읽으면
     * 실제 전송이 깨지고, 크기가 큰 바디(이미지 업로드 등)는 메모리에 이중으로 올라가므로 건너뛴다.
     */
    private fun readRequestBody(body: okhttp3.RequestBody): String {
        val length = body.contentLength()
        val loggable = !body.isOneShot() && !body.isDuplex() && length in 0..MAX_BODY_BYTES
        if (!loggable) return "<body 생략(${if (length < 0) "unknown" else length}B, one-shot/duplex 가능)>"
        return runCatching {
            Buffer().use { buffer ->
                body.writeTo(buffer)
                buffer.readUtf8()
            }
        }.getOrElse { "<unreadable body>" }
    }

    /** JSON 이면 마스킹 후 pretty-print, 아니면 토큰류만 정규식으로 가린 원문(단, 길이 상한 적용). */
    private fun pretty(raw: String): String {
        val text =
            runCatching {
                val masked = mask(json.parseToJsonElement(raw))
                prettyJson.encodeToString(JsonElement.serializer(), masked)
            }.getOrElse { redactNonJson(raw) }
        return if (text.length > MAX_LOG_CHARS) text.take(MAX_LOG_CHARS) + "…(생략)" else text
    }

    /** 비-JSON(폼 인코딩 등) 본문에서 토큰류 `key=value` 의 값을 마스킹한다. */
    private fun redactNonJson(raw: String): String = raw.replace(SENSITIVE_TEXT_REGEX) { "${it.groupValues[1]}=***" }

    private fun mask(element: JsonElement): JsonElement =
        when (element) {
            is JsonObject -> {
                JsonObject(
                    element.mapValues { (key, value) ->
                        if (key.lowercase() in SENSITIVE && value is JsonPrimitive && value.isString) {
                            JsonPrimitive("***")
                        } else {
                            mask(value)
                        }
                    },
                )
            }

            is JsonArray -> {
                JsonArray(element.map { mask(it) })
            }

            else -> {
                element
            }
        }

    private companion object {
        const val TAG = "API"
        const val MAX_BODY_BYTES = 256L * 1024 // 256KB 까지만 미리보기
        const val MAX_LOG_CHARS = 8_000 // logcat 한 항목이 잘리지 않게 상한

        /** 로그에 평문으로 남기면 안 되는 필드(값을 `***` 로 대체). 키는 소문자로 대소문자 무시 비교한다. */
        val SENSITIVE =
            setOf(
                "idtoken",
                "id_token",
                "accesstoken",
                "access_token",
                "refreshtoken",
                "refresh_token",
                "registrationtoken",
                "registration_token",
                "password",
                "authorization",
            )

        /** 비-JSON 본문(폼 인코딩 등)에서 토큰류 값을 가리기 위한 정규식. */
        val SENSITIVE_TEXT_REGEX =
            Regex(
                "(?i)(id_?token|access_?token|refresh_?token|" +
                    "registration_?token|password|authorization)=[^&\\s]+",
            )

        val prettyJson = Json { prettyPrint = true }
    }
}
