package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanInputVO
import com.chillsam.courmy.course.entity.PlanPlaceInputVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatePlanUseCaseTest {
    private val repository: PlanRepository = mockk()
    private val useCase = CreatePlanUseCase(repository)
    private val minimalPlan = PlanInputVO(places = listOf(PlanPlaceInputVO(placeId = 1)))

    @Test
    fun `제목 사진 없이 장소 하나만으로 계획을 만든다`() =
        runTest {
            coEvery { repository.createPlan(minimalPlan) } returns 42L

            assertEquals(42L, useCase(minimalPlan))
            coVerify(exactly = 1) { repository.createPlan(minimalPlan) }
        }

    @Test
    fun `경계 길이와 열 곳의 장소 및 도보 불가 구간을 허용한다`() =
        runTest {
            val plan =
                minimalPlan.copy(
                    title = "가".repeat(200),
                    memo = "나".repeat(500),
                    plannedDate = "2028-02-29",
                    sourceCourseId = 12,
                    places =
                        (1L..10L).map { id ->
                            PlanPlaceInputVO(id, memo = "다".repeat(500), walkingMinutes = if (id == 10L) null else -1)
                        },
                )
            coEvery { repository.createPlan(plan) } returns 43L

            assertEquals(43L, useCase(plan))
        }

    @Test
    fun `계약을 벗어난 입력은 서버 요청 전에 거절한다`() =
        runTest {
            val invalidPlans =
                listOf(
                    minimalPlan.copy(places = emptyList()),
                    minimalPlan.copy(places = (1L..11L).map { PlanPlaceInputVO(it, walkingMinutes = 0) }),
                    minimalPlan.copy(title = "가".repeat(201)),
                    minimalPlan.copy(memo = "가".repeat(501)),
                    minimalPlan.copy(sourceCourseId = 0),
                    minimalPlan.copy(sourceCourseId = -1),
                    minimalPlan.copy(plannedDate = "2026-02-29"),
                    minimalPlan.copy(plannedDate = "2026-9-20"),
                    minimalPlan.copy(plannedDate = ""),
                    minimalPlan.copy(places = listOf(PlanPlaceInputVO(0))),
                    minimalPlan.copy(places = listOf(PlanPlaceInputVO(-1))),
                    minimalPlan.copy(places = listOf(PlanPlaceInputVO(1, memo = "가".repeat(501)))),
                    minimalPlan.copy(places = listOf(PlanPlaceInputVO(1, walkingMinutes = -2))),
                    minimalPlan.copy(places = listOf(PlanPlaceInputVO(1), PlanPlaceInputVO(2))),
                )

            invalidPlans.forEach { plan ->
                val failure = runCatching { useCase(plan) }.exceptionOrNull()
                assertTrue("잘못된 계획을 거절해야 합니다: $plan", failure is IllegalArgumentException)
            }
            coVerify(exactly = 0) { repository.createPlan(any()) }
        }
}
