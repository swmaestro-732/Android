package com.chillsam.courmy.main.presentation.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeRegionCodesTest {
    @Test
    fun `map tiles use server region codes`() {
        assertEquals("29", homeRegionCode("29"))
        assertEquals("46", homeRegionCode("12"))
        assertEquals("47940", homeRegionCode("47U"))
        assertEquals("11", homeRegionCode("11"))
    }

    @Test
    fun `Gwangju and Jeonnam do not include each others districts`() {
        for (district in listOf("12210", "12240", "12270", "12300", "12330")) {
            assertTrue(isHomeSubRegion("29", district))
            assertFalse(isHomeSubRegion("46", district))
        }
        assertTrue(isHomeSubRegion("46", "12110"))
        assertFalse(isHomeSubRegion("29", "12110"))
        assertFalse(isHomeSubRegion("46", "11110"))
        assertTrue(isHomeSubRegion("11", "11110"))
        assertTrue(isHomeSubRegion("47940", "47940"))
        assertFalse(isHomeSubRegion("47940", "47111"))
    }
}
