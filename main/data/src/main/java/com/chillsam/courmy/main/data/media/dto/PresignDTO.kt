package com.chillsam.courmy.main.data.media.dto

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/uploads/presign` 요청.
 *
 * 배포된 swagger 는 `userId` 를 필수 쿼리 파라미터로 표시하지만, 서버 실제 시그니처는
 * `@CurrentUserId userId: Long` 이라 JWT 에서 가져온다(springdoc 오표기 — `isFollowing`→`following` 과 동일 유형).
 * 따라서 요청에 userId 를 싣지 않는다.
 */
@Serializable
data class PresignRequest(
    val purpose: String,
    val contentType: String,
    val contentLength: Long,
)

@Serializable
data class PresignEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PresignDTO? = null,
)

/**
 * [uploadUrl] 은 S3 프리사인 URL(우리 API 호스트가 아님)이라 Authorization 을 붙이지 않는다
 * — NetworkModule 의 authInterceptor 가 호스트를 확인해 자동으로 제외한다.
 * 업로드 완료 후 [imageUrl] 을 프로필의 profileImageUrl 로 쓴다.
 */
@Serializable
data class PresignDTO(
    val key: String? = null,
    val uploadUrl: String? = null,
    val imageUrl: String? = null,
)

/** 업로드 용도. 서버 enum 과 문자열이 일치해야 한다. */
object PresignPurpose {
    const val PROFILE = "PROFILE"
}
