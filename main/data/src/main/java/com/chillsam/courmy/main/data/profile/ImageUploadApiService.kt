package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.main.data.profile.dto.PresignEnvelope
import com.chillsam.courmy.main.data.profile.dto.PresignRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

/** 이미지 업로드용 S3 프리사인 URL 발급. 우리 API 호스트라 인증 토큰이 붙는다. */
interface ImageUploadApiService {
    @POST("api/v1/uploads/presign")
    suspend fun presign(
        @Body body: PresignRequest,
    ): Response<PresignEnvelope>
}

/**
 * 발급받은 프리사인 URL 로 바이트를 직접 올린다.
 *
 * S3 로 가는 요청이라 **인증 헤더가 붙지 않는 bare 클라이언트**를 쓴다. Content-Type 은 프리사인
 * 서명에 포함되므로 발급 때 보낸 값과 정확히 같아야 하며, 다르면 S3 가 403 으로 거절한다.
 */
interface S3UploadApiService {
    @PUT
    suspend fun upload(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String,
        @Body body: RequestBody,
    ): Response<ResponseBody>
}
