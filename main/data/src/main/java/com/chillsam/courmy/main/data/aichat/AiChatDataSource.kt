package com.chillsam.courmy.main.data.aichat

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.aichat.dto.AiChatEnvelope
import com.chillsam.courmy.main.data.aichat.dto.AiChatMessageRequest

class AiChatDataSource(
    private val apiService: AiChatApiService,
) : BaseRemoteDataSource() {
    /**
     * 답변을 보내고 다음 턴을 받는다. HTTP 실패(401·404(4400)·426(4260)·400)는 checkResponse 가
     * HttpResponseException 으로 던지고, 여기서 code != 2000 인 성공 바디도 실패로 본다.
     */
    suspend fun sendMessage(request: AiChatMessageRequest): AiChatEnvelope =
        checkResponse(apiService.sendMessage(request)).also { envelope ->
            check(envelope.code == SUCCESS_CODE) { envelope.message ?: "커미 AI 응답에 실패했습니다." }
        }

    private companion object {
        const val SUCCESS_CODE = 2000
    }
}
