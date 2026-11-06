package com.chillsam.courmy.main.presentation.aichat

import com.chillsam.courmy.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 커미 AI 코스 추천 채팅 화면 상태.
 *
 * 화면은 상단바·입력창이 항상 보이고, 가운데 대화 목록만 [messages] 로 채워진다.
 */
data class AiCourseChatUIState(
    val messages: ImmutableList<AiChatMessage> = persistentListOf(),
    /** 입력창 위에 뜨는 빠른 답변 칩. 비어 있으면 숨긴다. */
    val quickReplies: ImmutableList<String> = persistentListOf(),
    /** AI "입력 중…" 말풍선 노출 여부. */
    val isAiTyping: Boolean = false,
    /** 입력(전송·칩) 허용 여부. AI 응답 생성 중에는 false. */
    val inputEnabled: Boolean = true,
) : UiState {
    companion object {
        val empty: AiCourseChatUIState = AiCourseChatUIState()
    }
}
