package com.chillsam.courmy.main.data.aichat

import com.chillsam.courmy.main.data.aichat.dto.AiChatEnvelope
import com.chillsam.courmy.main.data.aichat.dto.AiChatMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiChatApiService {
    /**
     * 커미 AI 채팅의 유일한 엔드포인트. 답변(칩/직접 입력)을 보내고 다음 턴을 받는다.
     * 로그인 전용(Authorization 은 인터셉터가 붙인다). 세션 id 는 요청/응답으로 이어간다.
     */
    @POST("service/v1/ai-chat/messages")
    suspend fun sendMessage(
        @Body request: AiChatMessageRequest,
    ): Response<AiChatEnvelope>
}
