package com.chillsam.courmy.course.data.place.dto

import org.junit.Assert.assertEquals
import org.junit.Test

/** 등록 장소 검색(`GET /api/v1/places`) 응답 → 코스에 담을 장소 변환 검증. */
class PlaceSearchDtoMappingTest {
    @Test
    fun `장소 정보와 상세 주소를 후보 VO 로 변환한다`() {
        val vo =
            PlaceDTO(
                id = 101L,
                name = "어니언 성수",
                categories = listOf("CAFE"),
                address = "서울 성동구 아차산로9길 8",
            ).toVO()

        assertEquals("101", vo.id)
        assertEquals("어니언 성수", vo.name)
        assertEquals("카페·디저트", vo.category)
        assertEquals("서울 성동구 아차산로9길 8", vo.address)
    }

    @Test
    fun `주소가 없으면 빈 문자열로 두어 UI 가 주소 줄을 생략하게 한다`() {
        assertEquals("", PlaceDTO(id = 1L, name = "어니언").toVO().address)
    }
}
