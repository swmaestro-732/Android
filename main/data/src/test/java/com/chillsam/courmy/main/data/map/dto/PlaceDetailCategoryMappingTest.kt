package com.chillsam.courmy.main.data.map.dto

import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlaceDetailCategoryMappingTest {
    @Test
    fun `지도 응답의 상세 분류 48종을 대소문자에 관계없이 전달한다`() {
        PlaceDetailCategoryVO.entries.forEach { expected ->
            listOf(expected.name, expected.code, " ${expected.name} ").forEach { code ->
                val dto =
                    Json.decodeFromString<PlaceMapDTO>(
                        """
                        {"places":[{"id":1,"category":"CAFE","detailCategory":"$code",
                        "latitude":37.5,"longitude":127.0}]}
                        """.trimIndent(),
                    )
                assertEquals(
                    expected,
                    dto
                        .toVO()
                        .places
                        .single()
                        .detailCategory,
                )
                assertEquals(
                    "카페·디저트",
                    dto
                        .toVO()
                        .places
                        .single()
                        .category,
                )
            }
        }
    }

    @Test
    fun `상세값 누락 null 빈 문자열 미지원 코드는 기존 카테고리를 보존한다`() {
        listOf("", ",\"detailCategory\":null", ",\"detailCategory\":\"\"", ",\"detailCategory\":\"FUTURE\"")
            .forEach { field ->
                val dto =
                    Json.decodeFromString<PlaceMapDTO>(
                        """{"places":[{"id":1,"category":"CAFE","latitude":37.5,"longitude":127.0$field}]}""",
                    )
                val place = dto.toVO().places.single()
                assertNull(place.detailCategory)
                assertEquals("카페·디저트", place.category)
            }
    }

    @Test
    fun `두 검색 경로도 상세 분류를 지도 VO로 전달한다`() {
        val search =
            Json.decodeFromString<MapPlaceSearchDTO>(
                """{"places":[{"id":1,"categories":["CAFE"],"detailCategory":"BAKERY"}]}""",
            )
        val external =
            Json.decodeFromString<ExternalMapPlaceSearchDTO>(
                """{"places":[{"id":1,"category":"CAFE","detailCategory":"BAKERY"}]}""",
            )
        assertEquals(
            PlaceDetailCategoryVO.BAKERY,
            search
                .toPageVO()
                .items
                .single()
                .detailCategory,
        )
        assertEquals(
            PlaceDetailCategoryVO.BAKERY,
            external
                .toPageVO()
                .items
                .single()
                .detailCategory,
        )
    }

    @Test
    fun `주소나 subCategory는 상세 아이콘 분류로 추측하지 않는다`() {
        val place =
            ExternalMapPlaceSearchDTO(
                places = listOf(ExternalMapPlaceSearchItemDTO(id = 1, category = "CAFE", address = "BAKERY")),
            ).toPageVO().items.single()
        assertNull(place.detailCategory)
    }
}
