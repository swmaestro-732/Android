package com.chillsam.courmy.common.data.media.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/uploads/presigned-urls` 요청.
 *
 * 한 번에 여러 장을 요청할 수 있고, [images] 는 비어 있으면 안 되며 최대 [MAX_IMAGES_PER_REQUEST] 장이다
 * (서버 `@NotEmpty @Size(max = 10)`). 응답 [PresignItemDTO] 는 요청 순서와 같은 순서로 돌아온다.
 *
 * 사용자는 JWT 로 식별되므로 요청에 userId 를 싣지 않는다.
 */
@Serializable
data class PresignRequest(
    val purpose: String,
    val images: List<PresignImageRequest>,
)

/** 이미지 한 장의 메타. [contentLength] 는 0 이면 서버가 거부한다(`@Positive`). */
@Serializable
data class PresignImageRequest(
    val contentType: String,
    val contentLength: Long,
)

@Serializable
data class PresignEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PresignDTO? = null,
)

@Serializable
data class PresignDTO(
    val items: List<PresignItemDTO> = emptyList(),
)

/**
 * [uploadUrl] 은 S3 프리사인 URL(우리 API 호스트가 아님)이라 Authorization 을 붙이지 않는다
 * — NetworkModule 의 authInterceptor 가 호스트를 확인해 자동으로 제외한다.
 * 업로드 완료 후 [imageUrl] 을 프로필·코스 등에 저장할 공개 URL 로 쓴다.
 */
@Serializable
data class PresignItemDTO(
    val key: String? = null,
    val uploadUrl: String? = null,
    val imageUrl: String? = null,
)

/** 요청 1건에 담을 수 있는 최대 장수(서버 검증과 동일). */
const val MAX_IMAGES_PER_REQUEST = 10
