package com.chillsam.courmy.common.data.push

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT

@Serializable
data class RegisterDeviceRequest(
    val token: String,
    val platform: String,
    val appBuild: Long,
)

@Serializable
data class RemoveDeviceRequest(
    val token: String,
)

@Serializable
data class DeviceResponse(
    val code: Int? = null,
)

/** 비로그인 기기 갱신(SCRUM-756). null 필드는 서버가 바꾸지 않는다. */
@Serializable
data class UpdateDeviceRequest(
    val fcmToken: String? = null,
    val marketing: Boolean? = null,
    val night: Boolean? = null,
)

/** 동의 처리 시각. null 이면 미동의. 앱은 이 시각으로 처리 결과를 안내한다. */
@Serializable
data class DeviceConsentDTO(
    val marketingAgreedAt: String? = null,
    val nightAgreedAt: String? = null,
)

@Serializable
data class DeviceConsentResponse(
    val code: Int? = null,
    val data: DeviceConsentDTO? = null,
)

interface DeviceApi {
    @PUT("api/v1/my/devices")
    suspend fun register(
        @Body request: RegisterDeviceRequest,
    ): Response<DeviceResponse>

    @HTTP(method = "DELETE", path = "api/v1/my/devices", hasBody = true)
    suspend fun remove(
        @Body request: RemoveDeviceRequest,
    ): Response<DeviceResponse>

    /** 로그인 불필요. 기기는 X-Device-Id 헤더로 식별한다. */
    @PUT("api/v1/devices")
    suspend fun updateDevice(
        @Body request: UpdateDeviceRequest,
    ): Response<DeviceConsentResponse>

    /** 기기의 현재 동의 상태 조회. 로그인 불필요, X-Device-Id 로 식별한다. */
    @GET("api/v1/devices")
    suspend fun consent(): Response<DeviceConsentResponse>
}
