package com.chillsam.courmy.common.data.auth

import android.util.Base64
import org.json.JSONObject

/**
 * accessToken(JWT)의 payload 에서 sub(=userId)를 읽는다.
 * 서명 검증은 서버 몫이라 클라이언트는 payload(가운데 조각)만 base64url 디코드해 sub 를 파싱한다.
 * 토큰 형식이 어긋나거나 sub 가 없으면 null.
 */
internal fun userIdFromAccessToken(accessToken: String?): Long? {
    val payload = accessToken?.split(".")?.getOrNull(1) ?: return null
    return runCatching {
        val json = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
        JSONObject(json).optString("sub").toLongOrNull()
    }.getOrNull()
}
