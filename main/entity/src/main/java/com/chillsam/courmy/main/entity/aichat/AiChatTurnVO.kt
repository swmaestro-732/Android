package com.chillsam.courmy.main.entity.aichat

import com.chillsam.courmy.main.entity.home.HomeCourseVO

/**
 * 커미 AI 채팅 한 턴. 사용자의 답변을 보내면 서버가 이 모양으로 다음 턴을 돌려준다.
 *
 * [messages] 는 순서대로 커미 말풍선으로 추가하고, [quickReplies] 는 이전 칩을 지우고 교체한다.
 * [courses] 는 RESULT 단계에서만 채워진다(최대 3). 코스 카드는 홈 추천 코스와 같은 [HomeCourseVO] 라
 * 카드 UI 를 재사용한다.
 */
data class AiChatTurnVO(
    val sessionId: String,
    val step: AiChatStep,
    val messages: List<String>,
    val quickReplies: List<QuickReplyVO>,
    val input: AiChatInputVO,
    val courses: List<HomeCourseVO>,
)

/**
 * 지금 고를 수 있는 칩.
 *
 * [value] 는 되돌려 보낼 값이고 앱은 해석하지 않는다 — 유일한 예외는 `RESTART`(앱이 첫 화면으로 돌아감).
 * [requiresLocation] 이 true 면 현재 좌표를 답변에 실어 보내야 한다.
 */
data class QuickReplyVO(
    val label: String,
    val value: String,
    val requiresLocation: Boolean = false,
) {
    companion object {
        /** 앱이 가로채는 유일한 칩 값 — 첫 화면 복귀 + 세션 폐기. */
        const val VALUE_RESTART = "RESTART"
    }
}

/** 직접 입력창 상태. [enabled] 가 false 면 입력창을 잠근다. */
data class AiChatInputVO(
    val enabled: Boolean = true,
    val placeholder: String = "",
)
