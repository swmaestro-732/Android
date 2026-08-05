package com.chillsam.courmy.main.data.saved.dto

import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/my/saved-courses`(저장 레코드 목록) 응답 DTO.
 *
 * 화면 조합 API([SavedCourseEnvelope])와 달리 코스 요약 없이 저장 레코드만 준다.
 * 홈 피드 카드의 저장 여부를 표시하려고 id 만 필요할 때 쓴다(응답이 가볍다).
 */
@Serializable
data class SavedCourseIdsEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: SavedCourseIdsDTO? = null,
)

@Serializable
data class SavedCourseIdsDTO(
    val totalCount: Int? = null,
    val hasNext: Boolean = false,
    val savedCourses: List<SavedCourseIdItemDTO>? = null,
)

@Serializable
data class SavedCourseIdItemDTO(
    /** 저장 레코드 id(코스 id 가 아니다). */
    val id: Long? = null,
    val courseId: Long? = null,
)

/** 저장된 코스 id 집합. 화면은 이 집합에 있는지로 북마크 아이콘을 정한다. */
fun SavedCourseIdsDTO.toCourseIdSet(): Set<String> =
    savedCourses
        .orEmpty()
        .mapNotNull { it.courseId?.toString() }
        .toSet()
