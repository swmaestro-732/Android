package com.chillsam.courmy.main.entity.aichat

/**
 * 사용자가 커미에게 보내는 답변 한 건.
 *
 * - 첫 답변(지역)에서는 [sessionId] 가 null 이고 서버가 새 세션을 만들어 응답에 실어 준다.
 * - 앱이 생성한 구·시·군 지역 칩은 문구를 [text]로 보낸다.
 * - 이후 칩은 [value] 를 그대로, requiresLocation 칩은 [location] 을 붙인다.
 */
data class AiChatAnswer(
    val sessionId: String?,
    val type: AiChatAnswerType,
    val value: String? = null,
    val text: String? = null,
    val location: LatLng? = null,
)

/** 답변 종류. CHIP = 칩 선택(value), TEXT = 직접 입력(text). */
enum class AiChatAnswerType {
    CHIP,
    TEXT,
}

/** 현재 좌표. 위치 권한이 없으면 null 로 둔다. */
data class LatLng(
    val latitude: Double,
    val longitude: Double,
)
