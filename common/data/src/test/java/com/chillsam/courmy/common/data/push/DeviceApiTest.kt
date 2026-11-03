package com.chillsam.courmy.common.data.push

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT

class DeviceApiTest {
    @Test fun `기기 등록 body에 토큰 플랫폼 실제 빌드가 모두 포함된다`() {
        val json = Json { explicitNulls = false }
        val body =
            json
                .parseToJsonElement(
                    json.encodeToString(RegisterDeviceRequest("fake-token", "ANDROID", 12)),
                ).jsonObject
        assertEquals(setOf("token", "platform", "appBuild"), body.keys)
        assertEquals("ANDROID", body.getValue("platform").jsonPrimitive.content)
        assertEquals("12", body.getValue("appBuild").jsonPrimitive.content)
    }

    @Test fun `삭제는 토큰 경로 없이 DELETE body를 사용한다`() {
        val delete =
            DeviceApi::class.java.methods
                .first { it.name == "remove" }
                .getAnnotation(HTTP::class.java)
        val put =
            DeviceApi::class.java.methods
                .first { it.name == "register" }
                .getAnnotation(PUT::class.java)
        assertEquals("DELETE", delete.method)
        assertEquals(true, delete.hasBody)
        assertEquals("api/v1/my/devices", delete.path)
        assertEquals(delete.path, put.value)
        assertEquals("{\"token\":\"fake-token\"}", Json.encodeToString(RemoveDeviceRequest("fake-token")))
    }

    @Test fun `기기 갱신과 동의 조회는 api v1 devices 를 쓴다`() {
        val put =
            DeviceApi::class.java.methods
                .first { it.name == "updateDevice" }
                .getAnnotation(PUT::class.java)
        val get =
            DeviceApi::class.java.methods
                .first { it.name == "consent" }
                .getAnnotation(GET::class.java)
        assertEquals("api/v1/devices", put.value)
        assertEquals("api/v1/devices", get.value)
    }

    @Test fun `기기 갱신 body는 fcmToken 광고 야간 필드를 쓰고 null 은 생략한다`() {
        val json = Json { explicitNulls = false }
        val body =
            json
                .parseToJsonElement(
                    json.encodeToString(UpdateDeviceRequest(fcmToken = "fake-token", marketing = true, night = null)),
                ).jsonObject
        assertEquals(setOf("fcmToken", "marketing"), body.keys)
        assertEquals("fake-token", body.getValue("fcmToken").jsonPrimitive.content)
        assertEquals("true", body.getValue("marketing").jsonPrimitive.content)
    }
}
