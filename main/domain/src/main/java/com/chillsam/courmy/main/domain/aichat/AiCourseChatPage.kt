package com.chillsam.courmy.main.domain.aichat

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 커미 AI 코스 추천 채팅 화면 식별자.
 *
 * 홈(FS-09)의 "커미 AI" 배너("코스 추천받기")에서 진입한다.
 * 사용자가 AI 와 문답하며 취향을 좁혀 코스를 추천받는 대화형 화면이다.
 *
 * 실제 대화는 SendAiChatMessageUseCase를 통해 서버 응답으로 진행한다.
 */
object AiCourseChatPage : Page {
    const val PATH = "/ai/course-chat"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
