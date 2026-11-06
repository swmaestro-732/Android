package com.chillsam.courmy.main.presentation.aichat

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 커미 AI 코스 추천 채팅 화면의 사용자 입력. */
sealed interface AiCourseChatIntent : MviIntent {
    /** 최초 진입. 대화가 비어 있을 때만 첫 인사를 띄운다(재진입 중복 방지). */
    data object Start : AiCourseChatIntent

    /** 입력창에서 직접 보낸 자유 텍스트. */
    data class SendText(
        val text: String,
    ) : AiCourseChatIntent

    /** 추천 칩(빠른 답변)을 눌렀을 때. [value] 는 칩 라벨 그대로. */
    data class QuickReply(
        val value: String,
    ) : AiCourseChatIntent

    /** 추천 결과에서 "다시 추천받기". 대화를 초기화하고 처음부터 시작한다. */
    data object Restart : AiCourseChatIntent
}
