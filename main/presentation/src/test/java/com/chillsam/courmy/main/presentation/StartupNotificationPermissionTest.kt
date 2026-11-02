package com.chillsam.courmy.main.presentation

import org.junit.Assert.assertEquals
import org.junit.Test

class StartupNotificationPermissionTest {
    @Test
    fun `only consented, unanswered and opted in users are prompted at startup`() {
        for (granted in listOf(false, true)) {
            for (requested in listOf(false, true)) {
                for (enabled in listOf(false, true)) {
                    for (consented in listOf(false, true)) {
                        assertEquals(
                            "granted=$granted requested=$requested enabled=$enabled consented=$consented",
                            listOf(granted, requested, enabled, consented) == listOf(false, false, true, true),
                            shouldRequestStartupNotificationPermission(granted, requested, enabled, consented),
                        )
                    }
                }
            }
        }
    }
}
