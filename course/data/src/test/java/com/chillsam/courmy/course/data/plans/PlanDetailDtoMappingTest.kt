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
