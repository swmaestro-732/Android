package com.chillsam.courmy.main.data.profile

import android.content.Context
import android.net.Uri
import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.profile.dto.PresignRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 로컬 이미지( `content://` 포토피커 URI 등 )를 S3 로 올리고 공개 URL 을 돌려준다.
 *
 * 흐름은 3단계다: 프리사인 발급 → 발급된 URL 로 PUT → 응답의 `imageUrl` 사용.
 * 프리사인 서명에 Content-Type·Content-Length 가 포함되므로, 발급 때 보낸 값과
 * 실제 PUT 바디가 정확히 일치해야 한다. 그래서 바이트를 먼저 모두 읽어 길이를 확정한 뒤 발급받는다.
 */
class ImageUploadDataSource(
    private val context: Context,
    private val apiService: ImageUploadApiService,
    private val s3ApiService: S3UploadApiService,
) : BaseRemoteDataSource() {
    suspend fun uploadProfileImage(localUri: String): String {
        val uri = Uri.parse(localUri)
        val bytes =
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("이미지를 읽지 못했습니다: $localUri")
        val contentType = context.contentResolver.getType(uri) ?: DEFAULT_CONTENT_TYPE

        val envelope =
            checkResponse(
                apiService.presign(
                    PresignRequest(
                        purpose = PURPOSE_PROFILE,
                        contentType = contentType,
                        contentLength = bytes.size.toLong(),
                    ),
                ),
            )
        val presign =
            requireNotNull(envelope.data) {
                envelope.message ?: "업로드 URL 응답에 data 가 없습니다."
            }
        val uploadUrl = requireNotNull(presign.uploadUrl) { "업로드 URL 이 비어 있습니다." }
        val imageUrl = requireNotNull(presign.imageUrl) { "이미지 URL 이 비어 있습니다." }

        checkResponse(
            s3ApiService.upload(
                uploadUrl = uploadUrl,
                contentType = contentType,
                body = bytes.toRequestBody(contentType.toMediaType()),
            ),
        )
        return imageUrl
    }

    private companion object {
        /** 서버 `UploadPurpose` enum 이름과 일치해야 한다. */
        const val PURPOSE_PROFILE = "PROFILE"
        const val DEFAULT_CONTENT_TYPE = "image/jpeg"
    }
}
