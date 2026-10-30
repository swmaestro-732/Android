package com.chillsam.courmy.main.presentation.update

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class OptionalUpdateReminderTest {
    private var date = LocalDate.of(2026, 10, 30)
    private var storedDate: String? = null
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val preferences =
        mockk<SharedPreferences> {
            every { getString(any(), any()) } answers { storedDate }
            every { edit() } returns editor
        }

    init {
        every { editor.putString(any(), any()) } answers {
            storedDate = secondArg()
            editor
        }
    }

    @Test
    fun `처음에는 안내를 막지 않고 같은날 새 객체에서도 나중에 선택을 유지한다`() {
        val reminder = OptionalUpdateReminder(preferences) { date }
        assertFalse(reminder.isDismissedToday())
        reminder.dismissForToday()
        val recreated = OptionalUpdateReminder(preferences) { date }
        assertTrue(recreated.isDismissedToday())
    }

    @Test
    fun `24시간이 지나기 전이라도 현지 날짜가 바뀌면 다시 안내할 수 있다`() {
        val reminder = OptionalUpdateReminder(preferences) { date }
        reminder.dismissForToday()
        date = date.plusDays(1)
        assertFalse(reminder.isDismissedToday())
    }
}
