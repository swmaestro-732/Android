package com.chillsam.courmy.common.presentation.helper

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StatusBarStateTest {
    @Test
    fun closingNestedScreensRestoresEachPreviousColor() {
        val state = StatusBarState()
        val screen = Any()
        val sheet = Any()
        val dialog = Any()

        state.set(screen, Color.White)
        state.set(sheet, Color.Blue)
        state.set(dialog, Color.Red)
        assertEquals(Color.Red, state.color)

        state.reset(dialog)
        assertEquals(Color.Blue, state.color)
        state.reset(sheet)
        assertEquals(Color.White, state.color)
        state.reset(screen)
        assertNull(state.color)
    }

    @Test
    fun disposingCoveredScreenDoesNotClearCurrentScreenOrRestoreDisposedOwner() {
        val state = StatusBarState()
        val outgoing = Any()
        val incoming = Any()

        state.set(outgoing, Color.White)
        state.set(incoming, Color.White)
        state.reset(outgoing)
        assertEquals(Color.White, state.color)
        state.reset(incoming)
        assertNull(state.color)
    }

    @Test
    fun updatingOwnerDoesNotLeaveAnOldColorToRestore() {
        val state = StatusBarState()
        val screen = Any()
        val overlay = Any()

        state.set(screen, Color.White)
        state.set(overlay, Color.Blue)
        state.set(overlay, Color.Red)
        state.reset(overlay)
        assertEquals(Color.White, state.color)
        state.reset(screen)
        assertNull(state.color)
    }

    @Test
    fun equalButDistinctOwnersRetainTheirOwnColors() {
        val state = StatusBarState()
        val screen = listOf("owner")
        val overlay = listOf("owner")

        state.set(screen, Color.White)
        state.set(overlay, Color.Blue)
        state.reset(overlay)
        assertEquals(Color.White, state.color)
    }
}
