package com.chillsam.courmy.course.data.placeSave.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/saved-places` 요청.
 *
 * 저장자는 JWT 로 식별한다. OpenAPI 는 `userId` 쿼리를 `required: true` 로 표기하지만 서버는
 * 토큰에서 꺼내며 쿼리를 무시한다(코스 저장·후기 작성과 같은 패턴).
 */
@Serializable
data class SavePlaceRequest(
    val placeId: Long,
)

/** 저장/취소/방문 처리 응답. data 없이 code·message 만 온다. */
@Serializable
data class PlaceSaveEnvelope(
    val code: Int? = null,
    val message: String? = null,
)
