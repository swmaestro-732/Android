package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.data.plans.dto.PlanDetailDTO
import com.chillsam.courmy.course.data.plans.dto.PlanDetailEnvelope
import com.chillsam.courmy.course.data.plans.dto.toVO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlanDetailDtoMappingTest {
    @Test
    fun `방문 순서로 정렬하고 메모 날짜 원본 코스와 이동 시간을 보존한다`() {
        val response =
            Json.decodeFromString<PlanDetailEnvelope>(
                """
                {"data":{"planId":42,"title":"토요일 산책","memo":"우산 챙기기",
                "plannedDate":"2026-10-10","sourceCourseId":12,"places":[
                {"placeId":205,"orderNo":1,"memo":"예약 확인","walkingMinutesToNext":null},
                {"placeId":101,"orderNo":0,"memo":"정문에서 만나기","walkingMinutesToNext":-1}
                ]}}
                """.trimIndent(),
            )

        val plan = requireNotNull(response.data).toVO()

        assertEquals(42L, plan.planId)
        assertEquals("토요일 산책", plan.title)
        assertEquals("우산 챙기기", plan.memo)
        assertEquals("2026-10-10", plan.plannedDate)
        assertEquals(12L, plan.sourceCourseId)
        assertEquals(listOf(101L, 205L), plan.places.map { it.placeId })
        assertEquals(listOf("정문에서 만나기", "예약 확인"), plan.places.map { it.memo })
        assertEquals(-1, plan.places.first().walkingMinutesToNext)
        assertNull(plan.places.last().walkingMinutesToNext)
    }

    /** BFF 가 함께 주는 장소 정보(이름·카테고리·주소·좌표) 매핑. 카테고리 코드는 한글 라벨로 바꾼다. */
    @Test
    fun `장소의 이름 카테고리 주소 좌표를 함께 옮긴다`() {
        val response =
            Json.decodeFromString<PlanDetailEnvelope>(
                """
                {"data":{"planId":2,"title":"","places":[
                {"placeId":1491,"orderNo":0,"name":"심학산 두부마을","categories":["RESTAURANT"],
                "address":"경기 파주시 교하로681번길 16",
                "location":{"latitude":37.709,"longitude":126.703}}
                ]}}
                """.trimIndent(),
            )

        val place = requireNotNull(response.data).toVO().places.single()

        assertEquals("심학산 두부마을", place.name)
        assertEquals("음식점", place.categoryLabel)
        assertEquals("경기 파주시 교하로681번길 16", place.address)
        assertEquals(37.709, requireNotNull(place.latitude), 1e-9)
        assertEquals(126.703, requireNotNull(place.longitude), 1e-9)
    }

    /** 장소 정보가 비어 와도(name null 등) 깨지지 않고 빈 값으로 남긴다. */
    @Test
    fun `장소 정보가 없으면 빈 값으로 둔다`() {
        val place =
            PlanDetailDTO(
                planId = 1,
                places =
                    listOf(
                        com.chillsam.courmy.course.data.plans.dto
                            .PlanDetailPlaceDTO(placeId = 9),
                    ),
            ).toVO().places.single()

        assertEquals("", place.name)
        assertEquals("", place.categoryLabel)
        assertEquals("", place.address)
        assertNull(place.latitude)
        assertNull(place.longitude)
    }

    @Test
    fun `원본 코스가 없거나 유효하지 않으면 복제 출처를 비운다`() {
        listOf(null, 0L, -1L).forEach { sourceId ->
            val plan = PlanDetailDTO(planId = 1, sourceCourseId = sourceId).toVO()

            assertNull(plan.sourceCourseId)
            assertEquals("나의 계획", plan.title)
            assertEquals("", plan.memo)
            assertEquals("", plan.plannedDate)
            assertEquals(emptyList<Any>(), plan.places)
        }
    }
}
