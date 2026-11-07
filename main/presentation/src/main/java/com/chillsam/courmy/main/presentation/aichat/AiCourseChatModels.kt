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
 * 빠른 답변 카드의 종류. UI 가 종류에 맞는 리딩 아이콘을 골라 큼직한 선택 카드로 그린다.
 * (질문 단계마다 성격이 달라 아이콘을 바꾼다 — 지역/테마/장소.)
 */
enum class QuickReplyKind {
    REGION,
    THEME,
    PLACE,
    GENERIC,
}

/**
 * 채팅 한 줄(말풍선).
 *
 * [recommendations] 가 있으면 AI 말풍선 아래에 코스 추천 카드(3개)를 함께 그린다.
 * 백엔드 계약이 생기기 전까지는 프레젠테이션 전용 모델이다(직렬화·DTO 없음).
 */
@Immutable
data class AiChatMessage(
    val id: Long,
    val role: ChatRole,
    val text: String,
    val recommendations: List<AiCourseRecommendation> = emptyList(),
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

/**
 * 추천 결과 3개를 배치하는 방식. 프리뷰 단계라 화면에서 토글로 바꿔 보며 고른다.
 *
 * - [CAROUSEL]  가로 스와이프 카드(큰 이미지 + 점 인디케이터)
 * - [FEATURED]  대표 1개 크게 + 나머지 2개 작게
 * - [STACKED]   이미지-탑 세로 카드 3개
 */
enum class AiRecLayout {
    CAROUSEL,
    FEATURED,
    STACKED,
    ;

    /** 토글에서 보여줄 짧은 라벨. */
    val label: String
        get() =
            when (this) {
                CAROUSEL -> "스와이프"
                FEATURED -> "대표+2"
                STACKED -> "세로"
            }
}
