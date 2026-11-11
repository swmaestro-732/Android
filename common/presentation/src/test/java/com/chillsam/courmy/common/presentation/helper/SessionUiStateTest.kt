package com.chillsam.courmy.common.presentation.helper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionUiStateTest {
    @Test
    fun `guest writes are blocked and dismissing or logging in does not replay them`() {
        val session = SessionUiState()
        var writes = 0
        repeat(3) { session.requireLogin { writes++ } }
        assertTrue(session.loginRequired)
        assertEquals(0, writes)
        session.dismissLoginRequired()
        assertFalse(session.loginRequired)
        session.requireLogin { writes++ }
        session.login()
        assertFalse(session.loginRequired)
        assertEquals(0, writes)
        session.requireLogin { writes++ }
        assertEquals(1, writes)
        session.logout()
        session.requireLogin { writes++ }
        assertTrue(session.loginRequired)
        assertEquals(1, writes)
    }
}
