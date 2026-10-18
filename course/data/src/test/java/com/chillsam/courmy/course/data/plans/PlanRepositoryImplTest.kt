package com.chillsam.courmy.course.data.plans

import com.chillsam.courmy.course.data.plans.dto.CreatePlanEnvelope
import com.chillsam.courmy.course.data.plans.dto.CreatePlanRequest
import com.chillsam.courmy.course.data.plans.dto.EditPlanRequest
import com.chillsam.courmy.course.data.plans.dto.PlanDetailEnvelope
import com.chillsam.courmy.course.data.plans.dto.PlanIdData
import com.chillsam.courmy.course.data.plans.dto.PlanListEnvelope
import com.chillsam.courmy.course.entity.PlanInputVO
import com.chillsam.courmy.course.entity.PlanPlaceInputVO
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response

class PlanRepositoryImplTest {
    private val api = RecordingPlanApi()
    private val repository = PlanRepositoryImpl(PlanDataSource(api))

    @Test
    fun `재정렬된 장소 순서와 도보 불가를 전송하고 마지막 도보는 비운다`() =
        runBlocking {
            val id =
                repository.createPlan(
                    PlanInputVO(
                        title = "  토요일  ",
                        memo = "  출발 전 확인  ",
                        plannedDate = "2026-09-20",
                        sourceCourseId = 12,
                        places =
                            listOf(
                                PlanPlaceInputVO(205, memo = "  웨이팅 확인  ", walkingMinutes = -1),
                                PlanPlaceInputVO(101, memo = "  ", walkingMinutes = 6),
                            ),
                    ),
                )

            val request = requireNotNull(api.request)
            assertEquals(42L, id)
            assertEquals("토요일", request.title)
            assertEquals("출발 전 확인", request.memo)
            assertEquals("2026-09-20", request.plannedDate)
            assertEquals(12L, request.sourceCourseId)
            assertEquals(listOf(205L, 101L), request.places.map { it.placeId })
            assertEquals(listOf(0, 1), request.places.map { it.orderNo })
            assertEquals("웨이팅 확인", request.places.first().memo)
            assertNull(request.places.last().memo)
            assertEquals(-1, request.places.first().walkingMinutes)
            assertNull(request.places.last().walkingMinutes)
        }

    @Test
    fun `자유 계획의 직렬화에는 코스 공개 정보가 없다`() =
        runBlocking {
            repository.createPlan(PlanInputVO(places = listOf(PlanPlaceInputVO(101))))
            val request = requireNotNull(api.request)
            val json = Json { encodeDefaults = true }
            val payload = json.parseToJsonElement(json.encodeToString(request)).jsonObject

            assertEquals("", request.title)
            assertNull(request.sourceCourseId)
            assertEquals(setOf("title", "memo", "plannedDate", "sourceCourseId", "places"), payload.keys)
            assertEquals(
                setOf("placeId", "orderNo", "memo", "walkingMinutes"),
                payload
                    .getValue("places")
                    .jsonArray
                    .single()
                    .jsonObject.keys,
            )
        }

    private class RecordingPlanApi : PlanApiService {
        var request: CreatePlanRequest? = null

        override suspend fun getPlanDetail(planId: Long): Response<PlanDetailEnvelope> = error("계획 생성 중 상세를 요청하면 안 됩니다")

        override suspend fun getPlans(
            cursor: String?,
            size: Int,
        ): Response<PlanListEnvelope> = error("계획 생성 중 목록을 요청하면 안 됩니다")

        override suspend fun createPlan(request: CreatePlanRequest): Response<CreatePlanEnvelope> {
            this.request = request
            return Response.success(CreatePlanEnvelope(data = PlanIdData(planId = 42)))
        }

        override suspend fun editPlan(
            planId: Long,
            request: EditPlanRequest,
        ): Response<CreatePlanEnvelope> = error("계획 생성 중 수정을 요청하면 안 됩니다")

        override suspend fun deletePlan(planId: Long): Response<CreatePlanEnvelope> = error("계획 생성 중 삭제를 요청하면 안 됩니다")
    }
}
