package com.chillsam.courmy.course.entity

/**
 * 임시저장 목록에 노출할 초안 1건 요약.
 *
 * - [id]             서버 코스 id. 초안도 발행 전 코스라 코스 id 를 그대로 쓴다(이어서 작성·삭제·갱신 키).
 * - [title]          코스 제목(서버가 빈 문자열을 줄 수 있어 목록 표시용 기본 제목으로 채운다)
 * - [savedAtMillis]  임시저장 시각(epoch millis). 서버 `createdAt` 을 변환한 값이며, 못 읽으면 0
 */
data class DraftSummaryVO(
    val id: Long,
    val title: String,
    val savedAtMillis: Long,
)
