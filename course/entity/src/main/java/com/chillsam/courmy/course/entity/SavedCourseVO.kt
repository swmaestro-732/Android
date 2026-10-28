package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 저장된 코스 1건의 목록 표시용 요약(제목 + 저장 시각).
 * 완성 코스(마이 화면)와 임시저장 코스(임시저장 목록) 양쪽에서 공통으로 쓴다.
 *
 * - [title]           코스 제목
 * - [createdAtMillis] 저장 시각(epoch millis)
 */
@Serializable
data class SavedCourseVO(
    val title: String,
    val createdAtMillis: Long,
)
