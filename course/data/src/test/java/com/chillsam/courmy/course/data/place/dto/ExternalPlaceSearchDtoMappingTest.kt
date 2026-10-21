package com.chillsam.courmy.course.data.place.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 외부 지도 검색 응답 → 코스에 담을 장소 변환 검증.
 *
 * 결과 id 는 서버가 저장하며 부여한 **내부 place id** 라 코스 생성 요청에 그대로 실린다.
 * id 가 없는 항목이 섞여 들어가면 생성 요청이 잘못된 장소를 가리킨다.
 */
class ExternalPlaceSearchDtoMappingTest {
    @Test
    fun `내부 place id 를 그대로 후보 id 로 쓴다`() {
        val vo =
            ExternalPlaceSearchDataDTO(
                places = listOf(ExternalPlaceDTO(id = 181L, name = "건국대학교", category = "학교")),
            ).toVOList().single()

        assertEquals("181", vo.id)
        assertEquals("건국대학교", vo.name)
        assertEquals("학교", vo.category)
    }

    /** 지도 결과에는 카테고리가 비는 경우가 있다. 그때 주소가 없으면 어떤 장소인지 구분되지 않는다. */
    @Test
    fun `카테고리가 없으면 도로명 주소를 보여 준다`() {
        val vo =
            ExternalPlaceSearchDataDTO(
                places =
                    listOf(
                        ExternalPlaceDTO(
                            id = 1L,
                            name = "어니언",
                            category = "",
                            roadAddress = "서울 성동구 아차산로 100",
                            address = "성수동2가 277-135",
                        ),
                    ),
            ).toVOList().single()

        assertEquals("서울 성동구 아차산로 100", vo.category)
    }

    @Test
    fun `도로명 주소가 없으면 지번 주소를 쓴다`() {
        val vo =
            ExternalPlaceSearchDataDTO(
                places = listOf(ExternalPlaceDTO(id = 1L, roadAddress = null, address = "성수동2가 277-135")),
            ).toVOList().single()

        assertEquals("성수동2가 277-135", vo.category)
    }

    @Test
    fun `id 가 없는 항목은 제외한다`() {
        val list =
            ExternalPlaceSearchDataDTO(
                places = listOf(ExternalPlaceDTO(id = null, name = "id 없음"), ExternalPlaceDTO(id = 2L)),
            ).toVOList()

        assertEquals(1, list.size)
        assertEquals("2", list.single().id)
    }

    @Test
    fun `places 가 없으면 빈 목록이다`() {
        assertTrue(ExternalPlaceSearchDataDTO(places = null).toVOList().isEmpty())
    }
}
