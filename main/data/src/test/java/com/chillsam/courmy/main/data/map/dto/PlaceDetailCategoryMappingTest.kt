package com.chillsam.courmy.main.data.map.dto

import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlaceDetailCategoryMappingTest {
    @Test
    fun `지도 응답의 상세 분류 73종을 대소문자에 관계없이 전달한다`() {
        PlaceDetailCategoryVO.entries.forEach { expected ->
            listOf(expected.name, expected.code, " ${expected.name} ").forEach { code ->
                val dto =
                    Json.decodeFromString<PlaceMapDTO>(
                        """
                        {"places":[{"id":1,"category":"CAFE","categoryDetail":"$code",
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
        listOf("", ",\"categoryDetail\":null", ",\"categoryDetail\":\"\"", ",\"categoryDetail\":\"FUTURE\"")
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
                """{"places":[{"id":1,"categories":["CAFE"],"categoryDetail":"BAKERY"}]}""",
            )
        val external =
            Json.decodeFromString<ExternalMapPlaceSearchDTO>(
                """{"places":[{"id":1,"category":"CAFE","categoryDetail":"BAKERY"}]}""",
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
    fun `categoryDetail 한글 라벨은 구분자 차이를 무시하고 VO로 매핑된다`() {
        mapOf(
            "카페" to PlaceDetailCategoryVO.CAFE,
            "제과·베이커리" to PlaceDetailCategoryVO.BAKERY,
            "제과,베이커리" to PlaceDetailCategoryVO.BAKERY,
            "미술·공예" to PlaceDetailCategoryVO.ART_CRAFT,
        ).forEach { (value, expected) ->
            val dto =
                Json.decodeFromString<PlaceMapDTO>(
                    """
                    {"places":[{"id":1,"category":"CAFE","categoryDetail":"$value",
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
        }
    }

    @Test
    fun `서버 categoryDetail 73종 한글 값 전부 올바른 상세 VO로 매핑된다`() {
        // 서버는 구분자로 쉼표를 쓴다(예: "제과,베이커리"). enum 라벨(가운뎃점)과 매칭돼야 한다.
        PlaceDetailCategoryVO.entries.forEach { expected ->
            val serverValue = expected.label.replace("·", ",")
            val dto =
                Json.decodeFromString<PlaceMapDTO>(
                    """
                    {"places":[{"id":1,"category":"CAFE","categoryDetail":"$serverValue",
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
        }
    }

    @Test
    fun `복합 라벨이 조각으로 쪼개져 와도 상세 VO로 매핑된다`() {
        // 서버가 "제과,베이커리"를 "베이커리"처럼 조각으로 내려줄 경우 대비.
        mapOf(
            "베이커리" to PlaceDetailCategoryVO.BAKERY,
            "제과" to PlaceDetailCategoryVO.BAKERY,
            "사우나" to PlaceDetailCategoryVO.BATHHOUSE_SAUNA,
            "명소" to PlaceDetailCategoryVO.TOURIST_ATTRACTION,
            "포토스튜디오" to PlaceDetailCategoryVO.PHOTO_STUDIO,
        ).forEach { (value, expected) ->
            val dto =
                Json.decodeFromString<PlaceMapDTO>(
                    """
                    {"places":[{"id":1,"category":"CAFE","categoryDetail":"$value",
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
        }
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
