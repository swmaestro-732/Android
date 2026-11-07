package com.chillsam.courmy.main.presentation.aichat

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

/** 채팅 화면의 내부 상태 변경 이벤트. 외부에서 직접 dispatch 하지 않는다. */
sealed interface AiCourseChatReducerEvent : ReducerEvent {
    /** 말풍선 한 줄 추가(사용자/AI 공통). */
    data class Append(
        val message: AiChatMessage,
    ) : AiCourseChatReducerEvent

    /** AI 가 "입력 중…" 표시를 켜거나 끈다. */
    data class SetTyping(
        val typing: Boolean,
    ) : AiCourseChatReducerEvent

    /** 대화 아래 빠른 답변 카드 목록을 교체한다(없으면 숨김). [kind] 로 리딩 아이콘을 정한다. */
    data class SetQuickReplies(
        val replies: List<String>,
        val kind: QuickReplyKind = QuickReplyKind.GENERIC,
    ) : AiCourseChatReducerEvent

    /** 입력 가능 여부. AI 가 답하는 중에는 중복 전송을 막으려 잠근다. */
    data class SetInputEnabled(
        val enabled: Boolean,
    ) : AiCourseChatReducerEvent

    /** 대화 전체를 비운다("다시 추천받기"). */
    data object Clear : AiCourseChatReducerEvent

    /** 추천 결과 배치 방식 변경. */
    data class SetLayout(
        val layout: AiRecLayout,
    ) : AiCourseChatReducerEvent
}
