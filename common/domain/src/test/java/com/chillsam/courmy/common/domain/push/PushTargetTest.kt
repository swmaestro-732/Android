package com.chillsam.courmy.common.domain.push

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PushTargetTest {
    private val valid =
        mapOf(
            "type" to "COURSE_COMMENTED",
            "targetType" to "COURSE",
            "targetId" to "42",
            "notificationId" to "99",
        )

    @Test fun `코스 알림과 팔로우 알림만 허용한다`() {
        assertEquals(42L, PushTarget.parse(valid)?.targetId)
        assertEquals(
            "USER",
            PushTarget.parse(valid + mapOf("type" to "USER_FOLLOWED", "targetType" to "USER"))?.targetType,
        )
        assertNull(PushTarget.parse(valid + ("targetType" to "USER")))
        assertNull(PushTarget.parse(valid + ("type" to "OPEN_URL")))
    }

    @Test fun `누락 음수 0 overflow URL 식별자는 거부한다`() {
        valid.keys.forEach { assertNull(PushTarget.parse(valid - it)) }
        listOf("0", "-1", "9223372036854775808", "https://example.com", "").forEach { id ->
            assertNull(PushTarget.parse(valid + ("targetId" to id)))
            assertNull(PushTarget.parse(valid + ("notificationId" to id)))
        }
    }
}
