package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.entity.category.PlaceCategoryVO
import com.chillsam.courmy.course.entity.PlaceReviewTagCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PlaceReviewTagCatalogTest {
    private val commonCodes =
        "friendly browsing helpful quick clean interior photo cozy spacious findable parking nowait solo revisit"
            .split(
                " ",
            )

    @Test
    fun `카테고리를 모르면 공통 14개만 보여준다`() {
        assertEquals(commonCodes, PlaceReviewTagCatalog.forCategories(emptyList()).map { it.code })
    }

    @Test
    fun `각 업종에는 해당 업종과 공통 항목만 표시한다`() {
        val expected =
            mapOf(
                PlaceCategoryVO.CAFE to "coffee dessert signature lingering outlets view",
                PlaceCategoryVO.RESTAURANT to "tasty fresh hearty value variety group",
                PlaceCategoryVO.BAR to "drinks snacks mood late date",
                PlaceCategoryVO.SHOPPING to "stylish exclusive discovery gifts",
                PlaceCategoryVO.CULTURE to "artwork curation viewing healing",
                PlaceCategoryVO.EXPERIENCE to "guidance beginner result materials focus",
                PlaceCategoryVO.NATURE to "sunset air rest pet family",
                PlaceCategoryVO.LANDMARK to "serene architecture peaceful",
                PlaceCategoryVO.ENTERTAINMENT to "exciting together immersive rainyday diverse datefun",
                PlaceCategoryVO.WELLNESS to "refreshed calm expert hygiene tranquil metime",
            )
        expected.forEach { (category, codes) ->
            assertEquals(
                commonCodes + codes.split(" "),
                PlaceReviewTagCatalog.forCategories(listOf(category)).map { it.code },
            )
        }
    }

    @Test
    fun `다중 업종과 중복 입력에도 공통 태그와 업종 태그가 중복되지 않는다`() {
        val codes =
            PlaceReviewTagCatalog
                .forCategories(
                    listOf(PlaceCategoryVO.CAFE, PlaceCategoryVO.BAR, PlaceCategoryVO.CAFE),
                ).map {
                    it.code
                }

        assertEquals(25, codes.size)
        assertEquals(codes.size, codes.distinct().size)
        assertEquals(
            commonCodes.toSet() +
                "coffee dessert signature lingering outlets view drinks snacks mood late date".split(" "),
            codes.toSet(),
        )
        assertFalse("datefun" in codes)
    }

    @Test
    fun `64종은 모두 유일한 소문자 서버 코드이며 비어 있는 문구가 없다`() {
        val tags = PlaceReviewTagCatalog.entries
        assertEquals(64, tags.size)
        assertEquals(64, tags.map { it.code }.distinct().size)
        tags.forEach { tag ->
            assertEquals(tag.name.lowercase(), tag.code)
            assertFalse(tag.label.isBlank())
            assertFalse(tag.icon.isBlank())
        }
    }
}
