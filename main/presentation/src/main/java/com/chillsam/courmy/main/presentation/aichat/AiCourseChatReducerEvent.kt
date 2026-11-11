package com.chillsam.courmy.main.presentation.aichat

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO

/** 채팅 화면의 내부 상태 변경 이벤트. 외부에서 직접 dispatch 하지 않는다. */
sealed interface AiCourseChatReducerEvent : ReducerEvent {
    /** 말풍선 한 줄 추가(사용자/AI 공통). */
    data class Append(
        val message: AiChatMessage,
    ) : AiCourseChatReducerEvent

    /** AI "입력 중…" 표시 토글. */
    data class SetTyping(
        val typing: Boolean,
    ) : AiCourseChatReducerEvent

    /** 서버 칩 목록 교체(THEME 이후). */
    data class SetQuickReplies(
        val replies: List<QuickReplyVO>,
    ) : AiCourseChatReducerEvent

    /** 입력창 상태(활성/안내 문구) 교체. */
    data class SetInput(
        val enabled: Boolean,
        val placeholder: String,
    ) : AiCourseChatReducerEvent

    /** 현재 단계 교체. */
    data class SetStep(
        val step: AiChatStep,
    ) : AiCourseChatReducerEvent

    /** REGION 단계에서 앱이 보여줄 지역 칩(구·시·군 이름) 교체. [show] 가 true 면 노출. */
    data class SetRegionChips(
        val chips: List<String>,
        val show: Boolean,
    ) : AiCourseChatReducerEvent

    /** 대화 초기화("처음부터"·세션 만료). 지역 칩은 ViewModel 이 다시 채운다. */
    data object Reset : AiCourseChatReducerEvent
}
