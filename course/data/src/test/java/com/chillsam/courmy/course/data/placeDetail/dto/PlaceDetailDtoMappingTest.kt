package com.chillsam.courmy.course.data.placeDetail.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceDetailDtoMappingTest {
    @Test
    fun `장소 정보와 카테고리를 화면 VO로 변환한다`() {
        val vo =
            PlaceDetailScreenDTO(
                place =
                    PlaceScreenDTO(
                        name = "어니언 성수",
                        categories = listOf("카페", "베이커리"),
                        address = "서울 성동구 아차산로 110",
                        location = PlaceLocationDTO(latitude = 37.5445, longitude = 127.0560),
                    ),
            ).toVO()

        assertEquals("어니언 성수", vo.name)
        assertEquals("카페 · 베이커리", vo.category)
        assertEquals("서울 성동구 아차산로 110", vo.address)
        assertEquals(37.5445, vo.latitude!!, 0.0)
        assertEquals(127.0560, vo.longitude!!, 0.0)
        assertTrue(vo.hasLocation)
    }

    @Test
    fun `좌표가 한쪽만 있으면 지도를 그리지 않도록 둘 다 비운다`() {
        val vo =
            PlaceDetailScreenDTO(
                place = PlaceScreenDTO(location = PlaceLocationDTO(latitude = 37.5445)),
            ).toVO()

        assertNull(vo.latitude)
        assertNull(vo.longitude)
        assertFalse(vo.hasLocation)
    }

    @Test
    fun `place가 없어도 빈 값으로 안전하게 변환한다`() {
        val vo = PlaceDetailScreenDTO().toVO()

        assertEquals("", vo.name)
        assertEquals("", vo.category)
        assertEquals("", vo.address)
        assertFalse(vo.hasLocation)
    }
}
