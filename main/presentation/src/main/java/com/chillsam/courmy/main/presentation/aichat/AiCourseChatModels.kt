package com.chillsam.courmy.main.presentation.aichat

import androidx.compose.runtime.Immutable

/** 대화 말풍선의 작성 주체. */
enum class ChatRole {
    /** 사용자가 입력한 말풍선(오른쪽, 초록). */
    USER,

    /** 커미 AI 가 보낸 말풍선(왼쪽, 아바타 동반). */
    AI,
}

/**
 * 채팅 한 줄(말풍선).
 *
 * [recommendation] 이 있으면 AI 말풍선 아래에 코스 추천 카드를 함께 그린다.
 * 백엔드 계약이 생기기 전까지는 프레젠테이션 전용 모델이다(직렬화·DTO 없음).
 */
@Immutable
data class AiChatMessage(
    val id: Long,
    val role: ChatRole,
    val text: String,
    val recommendation: AiCourseRecommendation? = null,
)

/**
 * AI 가 최종적으로 제안하는 코스 요약 카드. 홈 "인기 코스" 카드와 같은 정보 구성을 따른다.
 *
 * 실제 추천 API 가 붙으면 courseId 로 상세 화면까지 연결하지만,
 * 지금은 목업이라 식별자 없이 표시용 요약만 담는다.
 */
@Immutable
data class AiCourseRecommendation(
    /** 좌상단 태그(예: "성수 · 데이트"). */
    val tagLabel: String,
    /** 코스 제목(예: "성수 감성 카페 코스"). */
    val title: String,
    /** 스팟 수·소요 시간 요약(예: "4 스팟 · 3시간"). */
    val meta: String,
)
