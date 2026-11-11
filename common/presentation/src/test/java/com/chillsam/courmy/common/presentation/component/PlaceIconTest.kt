package com.chillsam.courmy.common.presentation.component

import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.common.presentation.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PlaceIconTest {
    @Test
    fun `모든 상세 분류는 상위 카테고리와 무관하게 상세 리소스를 선택한다`() {
        PlaceDetailCategoryVO.entries.forEach { detail ->
            assertEquals(placeIcon("", detail), placeIcon("CAFE", detail))
            assertNotEquals(R.drawable.ic_place_detail_default_pin_24, placeIcon("", detail))
        }
        assertEquals(R.drawable.ic_place_detail_bakery_24, placeIcon("카페·디저트", PlaceDetailCategoryVO.BAKERY))
        assertEquals(
            R.drawable.ic_place_detail_needlework_24,
            placeIcon("체험·클래스", PlaceDetailCategoryVO.NEEDLEWORK),
        )
    }

    @Test
    fun `카테고리 코드와 라벨은 일치하며 주소 일부를 분류로 추측하지 않는다`() {
        com.chillsam.courmy.common.entity.category.PlaceCategoryVO.entries
            // ETC(기타)는 상세·상위 모두 매칭되지 않는 폴백 핀 전용이라 전용 아이콘이 없다.
            .filter { it != com.chillsam.courmy.common.entity.category.PlaceCategoryVO.ETC }
            .forEach {
                assertEquals(placeIcon(it.code), placeIcon(it.label))
                assertNotEquals(R.drawable.ic_place_detail_default_pin_24, placeIcon(it.code))
            }
        assertEquals(R.drawable.ic_place_detail_default_pin_24, placeIcon("서울특별시 공원로 10"))
        assertEquals(R.drawable.ic_place_cafe_24, placeIcon("카페·디저트 · 문화·전시"))
    }

    @Test
    fun `상세값이 없으면 기존 카테고리 또는 기본 핀으로 폴백한다`() {
        assertEquals(R.drawable.ic_place_cafe_24, placeIcon("CAFE", null))
        assertEquals(R.drawable.ic_place_cafe_24, placeIcon("카페·디저트", null))
        assertEquals(R.drawable.ic_place_detail_default_pin_24, placeIcon("ETC", null))
        assertEquals(R.drawable.ic_place_detail_default_pin_24, placeIcon("", null))
    }
}
