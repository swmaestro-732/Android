package com.chillsam.courmy.main.data.aichat

import com.chillsam.courmy.main.data.aichat.dto.toRequest
import com.chillsam.courmy.main.data.aichat.dto.toVO
import com.chillsam.courmy.main.domain.aichat.AiChatRepository
import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO

class AiChatRepositoryImpl(
    private val dataSource: AiChatDataSource,
) : AiChatRepository {
    override suspend fun sendMessage(answer: AiChatAnswer): AiChatTurnVO {
        val envelope = dataSource.sendMessage(answer.toRequest())
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "커미 AI 응답에 data 가 없습니다."
            }
        return data.toVO()
    }
}
