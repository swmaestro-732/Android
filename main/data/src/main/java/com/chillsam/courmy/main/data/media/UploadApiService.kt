package com.chillsam.courmy.main.data.media

import com.chillsam.courmy.main.data.media.dto.PresignEnvelope
import com.chillsam.courmy.main.data.media.dto.PresignRequest
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface UploadApiService {
    /** S3 업로드용 프리사인 URL 발급. 사용자는 JWT 로 식별되므로 요청에 userId 를 싣지 않는다. */
    @POST("api/v1/uploads/presign")
    suspend fun presign(
        @Body request: PresignRequest,
    ): Response<PresignEnvelope>

    /**
     * 발급받은 프리사인 URL 에 이미지 바이트를 PUT 한다.
     * presign 요청과 **같은 Content-Type** 을 실어야 서명이 일치한다.
     * 절대 URL(S3 호스트)이라 [Url] 로 baseUrl 을 대체한다.
     */
    @PUT
    suspend fun uploadToPresignedUrl(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String,
        @Body body: RequestBody,
    ): Response<Unit>
}
