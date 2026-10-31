package com.chillsam.courmy.common.data.push

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
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

interface DeviceApi {
    @PUT("api/v1/my/devices")
    suspend fun register(
        @Body request: RegisterDeviceRequest,
    ): Response<DeviceResponse>

    @HTTP(method = "DELETE", path = "api/v1/my/devices", hasBody = true)
    suspend fun remove(
        @Body request: RemoveDeviceRequest,
    ): Response<DeviceResponse>
}
