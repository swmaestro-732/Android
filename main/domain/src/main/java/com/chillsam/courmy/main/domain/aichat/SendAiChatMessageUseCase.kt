package com.chillsam.courmy.main.domain.aichat

import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO
import javax.inject.Inject

/**
 * 커미에게 답변을 보내고 다음 턴을 받는다. 커미 AI 채팅의 모든 턴(지역·테마·장소·추천·재추천)이 이 하나를 탄다.
 *
 * `RESTART`(처음부터)는 서버에 보내지 않고 앱이 처리하므로 여기로 오지 않는다.
 */
class SendAiChatMessageUseCase
    @Inject
    constructor(
        private val repository: AiChatRepository,
    ) {
        suspend operator fun invoke(answer: AiChatAnswer): AiChatTurnVO = repository.sendMessage(answer)
    }
