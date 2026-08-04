package com.chillsam.courmy.common.data.media

import android.content.Context
import android.net.Uri
import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.common.data.media.dto.PresignRequest
import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

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
    override suspend fun uploadImage(
        localUri: String,
        purpose: UploadPurpose,
    ): String {
        val uri = Uri.parse(localUri)
        val contentType = context.contentResolver.getType(uri) ?: DEFAULT_CONTENT_TYPE
        // 서버 MediaService 가 확장자를 매핑하는 타입만 받는다. 그 외는 presign 이 415 로 떨어지므로
        // 올리기 전에 걸러 사유를 분명히 남긴다(최근 기기 사진은 HEIC 인 경우가 있다).
        require(contentType in SUPPORTED_CONTENT_TYPES) {
            "지원하지 않는 이미지 형식입니다($contentType). JPEG·PNG·WebP 만 올릴 수 있어요."
        }
        val bytes = readBytesUpTo(uri, MAX_UPLOAD_BYTES)

        val presign =
            requireNotNull(
                checkResponse(
                    apiService.presign(
                        PresignRequest(
                            purpose = purpose.name,
                            contentType = contentType,
                            contentLength = bytes.size.toLong(),
                        ),
                    ),
                ).data,
            ) { "presign 응답에 data 가 없습니다." }

        val uploadUrl = requireNotNull(presign.uploadUrl) { "presign 응답에 uploadUrl 이 없습니다." }
        val imageUrl = requireNotNull(presign.imageUrl) { "presign 응답에 imageUrl 이 없습니다." }

        // 서명이 Content-Type 을 포함하므로 presign 요청과 동일한 값을 실어야 한다.
        // S3 PUT 성공은 본문이 없으므로 상태 코드만 본다(checkResponse 는 2xx 에도 body 를 요구한다).
        checkSuccess(
            apiService.uploadToPresignedUrl(
                uploadUrl = uploadUrl,
                contentType = contentType,
                body = bytes.toRequestBody(contentType.toMediaType()),
            ),
        )
        return imageUrl
    }

    /**
     * [limit] 바이트까지만 읽고, 넘으면 중단한다.
     *
     * 포토피커로 고른 원본 사진은 수십 MB 일 수 있어 통째로 읽으면 presign 요청 전에 OOM 으로 죽는다.
     * 한도 초과를 판정해야 하므로 한도보다 한 바이트 더 읽을 때까지 진행한 뒤 크기를 확인한다.
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
        const val DEFAULT_CONTENT_TYPE = "image/jpeg"

        /** 서버 `MediaService` 가 확장자를 매핑하는 타입만 받는다(그 외는 415). */
        val SUPPORTED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")

        /** 클라이언트 제한. 서버 `maxUploadBytes`(10MiB)보다 엄격하게 잡는다. */
        const val MAX_UPLOAD_BYTES = 5 * 1024 * 1024

        const val BUFFER_SIZE = 8 * 1024
    }
}
