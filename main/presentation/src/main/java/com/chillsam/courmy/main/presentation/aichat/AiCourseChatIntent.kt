package com.chillsam.courmy.main.presentation.aichat

import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO

/** 커미 AI 채팅 화면의 사용자 입력. */
sealed interface AiCourseChatIntent : MviIntent {
    /** 최초 진입. 대화가 비어 있을 때만 앱 소유 인사말 + 지역 질문을 띄운다. */
    data object Start : AiCourseChatIntent

    /** 현재 좌표 확보(화면이 위치 권한 확인 후 전달). 지역 칩 생성·내 근처 전송에 쓴다. */
    data class ProvideLocation(
        val latitude: Double,
        val longitude: Double,
    ) : AiCourseChatIntent

    /** REGION 단계에서 앱이 그린 지역 칩을 눌렀을 때. "내 근처"는 CHIP NEAR_ME, 그 외는 문구를 TEXT 로. */
    data class RegionChipTap(
        val label: String,
    ) : AiCourseChatIntent

    /** 서버가 내려준 칩(THEME 이후)을 눌렀을 때. value 는 그대로, requiresLocation 이면 좌표를 붙여 전송. */
    data class QuickReplyTap(
        val reply: QuickReplyVO,
    ) : AiCourseChatIntent

    /** 직접 입력창에서 보낸 자유 텍스트. */
    data class SendText(
        val text: String,
    ) : AiCourseChatIntent

    /** "처음부터"(RESTART). 서버에 보내지 않고 첫 화면으로 돌아가며 세션을 버린다. */
    data object Restart : AiCourseChatIntent
}
