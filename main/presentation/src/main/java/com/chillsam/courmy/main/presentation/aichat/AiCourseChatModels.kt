package com.chillsam.courmy.main.presentation.aichat

import androidx.compose.runtime.Immutable
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** 대화 말풍선의 작성 주체. */
enum class ChatRole {
    /** 사용자가 입력·선택한 말풍선(오른쪽, 초록). */
    USER,

    /** 커미 AI 가 보낸 말풍선(왼쪽, 글래스). */
    AI,
}

/**
 * 채팅 한 줄.
 *
 * [courses] 가 있으면 AI 말풍선 아래에 추천 코스 카드(최대 3)를 함께 그린다(RESULT 턴).
 * 서버 응답을 그대로 반영하는 프레젠테이션 모델이다.
 */
@Immutable
data class AiChatMessage(
    val id: Long,
    val role: ChatRole,
    val text: String,
    val courses: ImmutableList<HomeCourseVO> = persistentListOf(),
)
