package com.chillsam.courmy.course.data.courseSave.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/saved-courses` 요청(구 `/api/v1/courses/save` 와 같은 바디 — swagger 의
 * `SaveCourseRequest` 그대로다. 저장하는 사용자는 JWT 로 식별되므로 싣지 않는다).
 * [folderId] 가 null 이면 "미분류"로 저장된다(서버가 NULL 을 허용).
 */
@Serializable
data class SaveCourseRequest(
    val courseId: Long,
    val folderId: Long? = null,
)

/** 저장/취소 응답. data 없이 code·message 만 온다. */
@Serializable
data class CourseSaveEnvelope(
    val code: Int? = null,
    val message: String? = null,
)
