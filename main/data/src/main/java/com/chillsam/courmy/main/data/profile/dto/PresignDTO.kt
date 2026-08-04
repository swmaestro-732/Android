package com.chillsam.courmy.main.data.profile.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/uploads/presign` 요청 DTO.
 *
 * [purpose] 는 서버 `UploadPurpose` enum 문자열과 일치해야 한다(현재 "PROFILE" 하나).
 * [contentLength] 는 프리사인 서명에 포함되므로, 실제로 PUT 할 바이트 수와 정확히 같아야 한다.
 */
@Serializable
data class PresignRequest(
    val purpose: String,
    val contentType: String,
    val contentLength: Long,
)

/** 프리사인 응답 봉투. `uploadUrl` 로 PUT 한 뒤 `imageUrl` 을 프로필 수정에 쓴다. */
@Serializable
data class PresignEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PresignDTO? = null,
)

@Serializable
data class PresignDTO(
    val key: String? = null,
    val uploadUrl: String? = null,
    val imageUrl: String? = null,
)
