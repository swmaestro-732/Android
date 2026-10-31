package com.chillsam.courmy.common.data.push

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
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
}
