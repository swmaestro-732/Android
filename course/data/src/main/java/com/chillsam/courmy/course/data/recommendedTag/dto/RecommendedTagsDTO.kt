package com.chillsam.courmy.course.data.recommendedTag.dto

import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/recommended-tags`(코스 생성 추천 태그) 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 */
@Serializable
data class RecommendedTagsEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: RecommendedTagsDTO? = null,
)

@Serializable
data class RecommendedTagsDTO(
    val tags: List<String>? = null,
)

/** 빈 문자열은 칩으로 렌더할 수 없어 버린다. */
fun RecommendedTagsDTO.toTagList(): List<String> = tags.orEmpty().filter { it.isNotBlank() }
