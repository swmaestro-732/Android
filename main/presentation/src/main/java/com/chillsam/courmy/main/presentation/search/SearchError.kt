package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.domain.error.HttpResponseException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** HTTP 오류의 서버 안내만 노출하고, 네트워크·파싱 예외의 내부 정보는 숨긴다. */
internal fun Throwable.searchErrorMessage(fallback: String): String =
    if (this is HttpResponseException) {
        runCatching {
            Json
                .parseToJsonElement(cause?.message.orEmpty())
                .jsonObject["message"]
                ?.jsonPrimitive
                ?.contentOrNull
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: fallback
    } else {
        fallback
    }
