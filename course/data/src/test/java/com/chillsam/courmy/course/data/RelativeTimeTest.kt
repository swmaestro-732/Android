package com.chillsam.courmy.course.data

import com.chillsam.courmy.common.entity.time.RelativeTime
import com.chillsam.courmy.common.entity.time.RelativeTimeUnit
import com.chillsam.courmy.common.entity.time.parseIsoUtcMillis
import com.chillsam.courmy.common.entity.time.relativeTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RelativeTimeTest {
    @Test
    fun `relative time preserves unit boundaries without display text`() {
        val cases =
            listOf(
                -1L to RelativeTime(RelativeTimeUnit.JUST_NOW, 0),
                59_999L to RelativeTime(RelativeTimeUnit.JUST_NOW, 0),
                60_000L to RelativeTime(RelativeTimeUnit.MINUTE, 1),
                3_599_999L to RelativeTime(RelativeTimeUnit.MINUTE, 59),
                3_600_000L to RelativeTime(RelativeTimeUnit.HOUR, 1),
                86_400_000L to RelativeTime(RelativeTimeUnit.DAY, 1),
                7 * 86_400_000L to RelativeTime(RelativeTimeUnit.WEEK, 1),
                30 * 86_400_000L to RelativeTime(RelativeTimeUnit.MONTH, 1),
                365 * 86_400_000L to RelativeTime(RelativeTimeUnit.YEAR, 1),
            )
        cases.forEach { (elapsed, expected) ->
            assertEquals(expected, relativeTime(epochMillis = 0, nowMillis = elapsed))
        }
    }

    @Test
    fun `UTC parsing discards fractional seconds without shifting minutes`() {
        assertEquals(1_789_689_600_000L, parseIsoUtcMillis("2026-09-18T00:00:00.923816Z"))
        assertNull(parseIsoUtcMillis(null))
        assertNull(parseIsoUtcMillis("invalid"))
    }
}
