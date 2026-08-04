package com.chillsam.courmy.main.data.media

import android.content.Context
import android.net.Uri
import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.media.dto.PresignPurpose
import com.chillsam.courmy.main.data.media.dto.PresignRequest
import com.chillsam.courmy.main.domain.media.MediaRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 프로필 이미지 업로드. presign 발급 → S3 PUT → 서버가 알려준 공개 URL 반환의 3단계다.
 *
 * presign 은 액세스 토큰이 필요하므로 **로그인/가입이 끝난 뒤에만** 호출할 수 있다
 * (가입 화면에서는 아직 registrationToken 뿐이라 불가 — [com.chillsam.courmy.main.domain.auth.SignupUseCase] 참고).
 */
class MediaRepositoryImpl(
    private val apiService: UploadApiService,
    private val context: Context,
) : BaseRemoteDataSource(),
    MediaRepository {
    override suspend fun uploadProfileImage(localUri: String): String {
        val uri = Uri.parse(localUri)
        val bytes =
            requireNotNull(context.contentResolver.openInputStream(uri)?.use { it.readBytes() }) {
                "이미지를 읽지 못했습니다: $localUri"
            }
        val contentType = context.contentResolver.getType(uri) ?: DEFAULT_CONTENT_TYPE

        val presign =
            requireNotNull(
                checkResponse(
                    apiService.presign(
                        PresignRequest(
                            purpose = PresignPurpose.PROFILE,
                            contentType = contentType,
                            contentLength = bytes.size.toLong(),
                        ),
                    ),
                ).data,
            ) { "presign 응답에 data 가 없습니다." }

        val uploadUrl = requireNotNull(presign.uploadUrl) { "presign 응답에 uploadUrl 이 없습니다." }
        val imageUrl = requireNotNull(presign.imageUrl) { "presign 응답에 imageUrl 이 없습니다." }

        // 서명이 Content-Type 을 포함하므로 presign 요청과 동일한 값을 실어야 한다.
        checkResponse(
            apiService.uploadToPresignedUrl(
                uploadUrl = uploadUrl,
                contentType = contentType,
                body = bytes.toRequestBody(contentType.toMediaType()),
            ),
        )
        return imageUrl
    }

    private companion object {
        const val DEFAULT_CONTENT_TYPE = "image/jpeg"
    }
}
