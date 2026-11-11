package com.chillsam.courmy.main.presentation.aichat

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 커미 AI 채팅 화면 상태.
 *
 * REGION 단계는 앱이 소유해 [regionChips] 를 쓰고, 그 뒤는 서버가 준 [quickReplies] 를 쓴다.
 * 둘 중 지금 그릴 쪽은 [showRegionChips] 로 가른다(REGION 이고 서버 칩이 비었을 때 지역 칩).
 */
data class AiCourseChatUIState(
    val messages: ImmutableList<AiChatMessage> = persistentListOf(),
    /** 서버가 내려준 칩(THEME 이후). */
    val quickReplies: ImmutableList<QuickReplyVO> = persistentListOf(),
    /** 앱이 위치 기준으로 만든 구·시·군 지역 칩 라벨. */
    val regionChips: ImmutableList<String> = persistentListOf(),
    /** true 면 서버 칩 대신 지역 칩을 그린다(REGION 단계). */
    val showRegionChips: Boolean = false,
    val step: AiChatStep = AiChatStep.REGION,
    val isAiTyping: Boolean = false,
    val inputEnabled: Boolean = true,
    val inputPlaceholder: String = DEFAULT_PLACEHOLDER,
) : UiState {
    companion object {
        const val DEFAULT_PLACEHOLDER = "메시지를 입력하세요"
        val empty: AiCourseChatUIState = AiCourseChatUIState()
    }
}
