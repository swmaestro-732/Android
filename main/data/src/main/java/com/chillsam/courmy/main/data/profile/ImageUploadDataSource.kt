package com.chillsam.courmy.main.data.profile

import android.content.Context
import android.net.Uri
import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.profile.dto.PresignRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

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
        val contentType = context.contentResolver.getType(uri) ?: DEFAULT_CONTENT_TYPE
        // 서버가 허용하지 않는 타입은 프리사인에서 415 로 떨어지므로, 먼저 걸러 사유를 분명히 남긴다.
        require(contentType in SUPPORTED_CONTENT_TYPES) {
            "지원하지 않는 이미지 형식입니다($contentType). JPEG·PNG·WebP 만 올릴 수 있어요."
        }
        val bytes = readBytesUpTo(uri, MAX_UPLOAD_BYTES)

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

        // S3 PUT 성공은 본문이 없으므로 상태 코드만 본다.
        checkSuccess(
            s3ApiService.upload(
                uploadUrl = uploadUrl,
                contentType = contentType,
                body = bytes.toRequestBody(contentType.toMediaType()),
            ),
        )
        return imageUrl
    }

    /**
     * [limit] 바이트까지만 읽고, 넘으면 즉시 중단한다.
     *
     * 포토피커로 고른 원본 사진은 수십 MB 일 수 있어 통째로 읽으면 OOM 으로 앱이 죽는다.
     * 한도를 넘는 순간 끊어야 하므로 한 바이트 더(`limit + 1`) 읽어 초과를 판정한다.
     */
    private fun readBytesUpTo(
        uri: Uri,
        limit: Int,
    ): ByteArray {
        val bytes =
            context.contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                val output = ByteArrayOutputStream()
                while (output.size() <= limit) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    output.write(buffer, 0, read)
                }
                output.toByteArray()
            } ?: error("이미지를 읽지 못했습니다: $uri")
        require(bytes.size <= limit) {
            "이미지가 너무 큽니다. ${limit / 1024 / 1024}MB 이하로 올려 주세요."
        }
        return bytes
    }

    private companion object {
        /** 서버 `UploadPurpose` enum 이름과 일치해야 한다. */
        const val PURPOSE_PROFILE = "PROFILE"
        const val DEFAULT_CONTENT_TYPE = "image/jpeg"

        /** 서버 `MediaService` 가 확장자를 매핑하는 타입만 받는다(그 외는 415). */
        val SUPPORTED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")

        /** 클라이언트 제한. 서버 `maxUploadBytes`(10MiB)보다 엄격하게 잡는다. */
        const val MAX_UPLOAD_BYTES = 5 * 1024 * 1024

        const val BUFFER_SIZE = 8 * 1024
    }
}
