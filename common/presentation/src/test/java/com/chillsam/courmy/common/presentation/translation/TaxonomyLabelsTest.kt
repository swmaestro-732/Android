package com.chillsam.courmy.common.presentation.translation

import org.junit.Assert.assertEquals
import org.junit.Test

class TaxonomyLabelsTest {
    private val labels =
        mapOf(
            "문화" to "Culture",
            "문화·전시" to "Culture & Exhibitions",
            "관광·명소" to "Landmarks",
            "카페" to "Cafe",
        )

    private fun translate(label: String): String = taxonomyLabelParts(label).joinToString("") { labels[it] ?: it }

    @Test
    fun compoundCategoryNamesStayIntact() {
        assertEquals("Culture & Exhibitions", translate("문화·전시"))
        assertEquals("Landmarks", translate("관광·명소"))
    }

    @Test
    fun combinedLabelsKeepTheirSeparators() {
        assertEquals("Culture & Exhibitions · Cafe", translate("문화·전시 · 카페"))
        assertEquals("Culture & Exhibitions·Landmarks", translate("문화·전시·관광·명소"))
    }

    @Test
    fun unknownLabelsAndWhitespaceArePreserved() {
        assertEquals("  Culture & Exhibitions · 새 분류  ", translate("  문화·전시 · 새 분류  "))
        assertEquals("Culture·전시관", translate("문화·전시관"))
        assertEquals("새문화·전시관", translate("새문화·전시관"))
        assertEquals("", translate(""))
    }
}
