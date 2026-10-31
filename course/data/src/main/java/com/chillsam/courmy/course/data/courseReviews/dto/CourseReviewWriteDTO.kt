package com.chillsam.courmy.course.data.courseReviews.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/courses/{courseId}/reviews` 요청.
 *
 * 작성자는 JWT 로 식별되므로 `userId` 를 싣지 않는다. OpenAPI 는 `userId` 쿼리를 `required: true` 로
 * 표기하지만 서버는 `@CurrentUserId` 로 토큰에서 꺼내며 쿼리를 보내도 무시한다.
 *
 * [content] 는 별점만 남길 때 null 이다. [photoUrls]·[tagCodes] 는 비어도 되지만 **null 은 안 된다**
 * (서버가 `emptyList()` 기본값을 두긴 하나, 명시해 보내야 의도가 분명하다).
 */
@Serializable
data class CreateCourseReviewRequest(
    val rating: Int,
    val content: String? = null,
    val photoUrls: List<String> = emptyList(),
    val tagCodes: List<String> = emptyList(),
)

@Serializable
data class CreateCourseReviewEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CreateCourseReviewData? = null,
)

@Serializable
data class CreateCourseReviewData(
    val reviewId: Long? = null,
)

/** 삭제 응답. `data` 는 항상 null 이라 code·message 만 받는다. */
@Serializable
data class CourseReviewDeleteEnvelope(
    val code: Int? = null,
    val message: String? = null,
)
