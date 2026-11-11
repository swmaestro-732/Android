package com.chillsam.courmy.main.domain.aichat

import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO

/**
 * 커미 AI 채팅의 유일한 계약 — 답변을 보내고 다음 턴을 받는다.
 * (`POST /service/v1/ai-chat/messages`, 서버 주도 대화. 백엔드 SCRUM-785.)
 */
interface AiChatRepository {
    /** 답변을 보내고 커미의 다음 턴을 받는다. 실패 시 예외를 던진다(처리는 상위에서). */
    suspend fun sendMessage(answer: AiChatAnswer): AiChatTurnVO
}
