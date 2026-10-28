package com.chillsam.courmy.main.data.map.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapPlaceSearchDtoMappingTest {
    @Test
    fun `응답의 저장 여부 평점 좌표와 커서를 보존한다`() {
        val response =
            Json.decodeFromString<MapPlaceSearchEnvelope>(
                """
                {"data":{"places":[{"id":42,"name":"성수 카페","averageRating":4.5,
                "hasSaved":true,"location":{"latitude":37.54,"longitude":127.05}}],
                "nextCursor":"cursor+/=","hasNext":true}}
                """.trimIndent(),
            )
        val page = requireNotNull(response.data).toPageVO()
        val place = page.items.single()

        assertEquals(42L, place.id)
        assertEquals("성수 카페", place.name)
        assertEquals(4.5, place.rating, 0.0)
        assertTrue(place.saved)
        assertEquals(37.54, requireNotNull(place.latitude), 0.0)
        assertEquals(127.05, requireNotNull(place.longitude), 0.0)
        assertEquals("cursor+/=", page.nextCursor)
        assertTrue(page.hasNext)
    }

    @Test
    fun `id 누락 항목과 중복 id를 제외한다`() {
        val page =
            MapPlaceSearchDTO(
                places =
                    listOf(
                        MapPlaceSearchItemDTO(name = "id 없음"),
                        MapPlaceSearchItemDTO(id = 1, name = "첫 항목"),
                        MapPlaceSearchItemDTO(id = 1, name = "중복"),
                    ),
            ).toPageVO()

        assertEquals(listOf(1L), page.items.map { it.id })
        assertEquals("첫 항목", page.items.single().name)
    }

    @Test
    fun `좌표가 한쪽이라도 없으면 두 좌표를 모두 비운다`() {
        listOf(
            null,
            MapPlaceSearchLocationDTO(latitude = 37.54),
            MapPlaceSearchLocationDTO(longitude = 127.05),
        ).forEach { location ->
            val place =
                MapPlaceSearchDTO(places = listOf(MapPlaceSearchItemDTO(id = 1, location = location)))
                    .toPageVO()
                    .items
                    .single()

            assertNull(place.latitude)
            assertNull(place.longitude)
            assertFalse(place.saved)
            assertEquals(0.0, place.rating, 0.0)
        }
    }

    @Test
    fun `다음 커서가 없으면 hasNext가 true여도 추가 조회하지 않는다`() {
        listOf(null, "", " \t").forEach { cursor ->
            val page = MapPlaceSearchDTO(nextCursor = cursor, hasNext = true).toPageVO()

            assertNull(page.nextCursor)
            assertFalse(page.hasNext)
            assertTrue(page.items.isEmpty())
        }
        assertFalse(MapPlaceSearchDTO(nextCursor = "last", hasNext = false).toPageVO().hasNext)
    }
}
