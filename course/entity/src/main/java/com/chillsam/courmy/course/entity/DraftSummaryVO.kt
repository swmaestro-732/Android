package com.chillsam.courmy.course.entity

/**
 * 임시저장 목록에 노출할 초안 1건 요약.
 *
 * - [id]             초안 식별자. 제목이 바뀌어도 같은 초안을 가리켜 중복 저장을 막는다.
 * - [title]          코스 제목(이름이 비면 기본 제목)
 * - [savedAtMillis]  마지막 임시저장 시각(epoch millis)
 */
data class DraftSummaryVO(
    val id: String,
    val title: String,
    val savedAtMillis: Long,
)
