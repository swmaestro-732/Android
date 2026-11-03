package com.chillsam.courmy.main.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class StartupNotificationPermissionTest {
    @Test
    fun `only consented, unanswered and opted in users are prompted at startup`() {
        // granted·requested·enabled·consented 네 불리언의 16가지 조합을 모두 확인한다.
        // 비트마스크로 펼쳐 중첩 루프 없이 한 번에 돈다.
        repeat(1 shl 4) { mask ->
            val granted = mask and 0b0001 != 0
            val requested = mask and 0b0010 != 0
            val enabled = mask and 0b0100 != 0
            val consented = mask and 0b1000 != 0
            assertEquals(
                "granted=$granted requested=$requested enabled=$enabled consented=$consented",
                !granted && !requested && enabled && consented,
                shouldRequestStartupNotificationPermission(granted, requested, enabled, consented),
            )
        }
    }
}
