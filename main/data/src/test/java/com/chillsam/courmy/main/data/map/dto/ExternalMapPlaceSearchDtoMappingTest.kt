package com.chillsam.courmy.main.data.map.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExternalMapPlaceSearchDtoMappingTest {
    @Test
    fun `외부 검색 응답의 평면 좌표와 주소를 지도 항목으로 변환한다`() {
        val response =
            Json { ignoreUnknownKeys = true }.decodeFromString<ExternalMapPlaceSearchEnvelope>(
                """
                {"code":200,"message":"성공","data":{"places":[{
                "id":42,"name":"성수 카페","category":"cafe",
                "roadAddress":"서울 성동구 성수이로 1","address":"서울 성동구 성수동 1",
                "latitude":37.54,"longitude":127.05,"telephone":"02-123-4567"
                }]}}
                """.trimIndent(),
            )
        val page = requireNotNull(response.data).toPageVO()
        val place = page.items.single()

        assertEquals(42L, place.id)
        assertEquals("성수 카페", place.name)
        assertEquals("카페·디저트", place.category)
        assertEquals("서울 성동구 성수이로 1", place.subCategory)
        assertEquals(37.54, requireNotNull(place.latitude), 0.0)
        assertEquals(127.05, requireNotNull(place.longitude), 0.0)
        assertFalse(place.saved)
        assertEquals(0.0, place.rating, 0.0)
        assertNull(page.nextCursor)
        assertFalse(page.hasNext)
    }

    @Test
    fun `양수가 아닌 id와 id 없는 항목을 제외하고 중복을 제거한다`() {
        val page =
            ExternalMapPlaceSearchDTO(
                places =
                    listOf(
                        ExternalMapPlaceSearchItemDTO(name = "id 없음"),
                        ExternalMapPlaceSearchItemDTO(id = 0, name = "0"),
                        ExternalMapPlaceSearchItemDTO(id = -1, name = "음수"),
                        ExternalMapPlaceSearchItemDTO(id = 1, name = "첫 항목"),
                        ExternalMapPlaceSearchItemDTO(id = 1, name = "중복"),
                    ),
            ).toPageVO()

        assertEquals(listOf(1L), page.items.map { it.id })
        assertEquals("첫 항목", page.items.single().name)
    }

    @Test
    fun `도로명 주소가 없거나 공백이면 지번 주소를 표시한다`() {
        listOf(null, "", " \t").forEach { roadAddress ->
            val place =
                ExternalMapPlaceSearchDTO(
                    places =
                        listOf(
                            ExternalMapPlaceSearchItemDTO(
                                id = 1,
                                roadAddress = roadAddress,
                                address = "서울 성동구 성수동 1",
                            ),
                        ),
                ).toPageVO().items.single()

            assertEquals("서울 성동구 성수동 1", place.subCategory)
        }
    }

    @Test
    fun `좌표가 한쪽이라도 누락되면 두 좌표를 모두 비운다`() {
        listOf(
            ExternalMapPlaceSearchItemDTO(id = 1),
            ExternalMapPlaceSearchItemDTO(id = 1, latitude = 37.54),
            ExternalMapPlaceSearchItemDTO(id = 1, longitude = 127.05),
        ).forEach { item ->
            val place = ExternalMapPlaceSearchDTO(places = listOf(item)).toPageVO().items.single()

            assertNull(place.latitude)
            assertNull(place.longitude)
        }
    }

    @Test
    fun `장소 목록이 없으면 추가 페이지 없는 빈 결과를 반환한다`() {
        val page = ExternalMapPlaceSearchDTO().toPageVO()

        assertTrue(page.items.isEmpty())
        assertNull(page.nextCursor)
        assertFalse(page.hasNext)
    }
}
