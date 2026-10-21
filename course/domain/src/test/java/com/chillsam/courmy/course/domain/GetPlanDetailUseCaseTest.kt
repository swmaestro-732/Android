package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanDetailPlaceVO
import com.chillsam.courmy.course.entity.PlanDetailVO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * 계획 상세 BFF 가 장소 정보까지 내려주므로 UseCase 는 위임만 한다 —
 * 과거의 장소별 병렬 조회(N+1)·부분 실패 폴백 로직이 되살아나지 않게 가드한다.
 */
class GetPlanDetailUseCaseTest {
    private val plans: PlanRepository = mockk()
    private val useCase = GetPlanDetailUseCase(plans)

    @Test
    fun `계획 상세를 그대로 돌려준다`() =
        runTest {
            val plan =
                PlanDetailVO(
                    planId = 42,
                    title = "산책",
                    sourceCourseId = 12,
                    places =
                        listOf(
                            PlanDetailPlaceVO(1, memo = "먼저", name = "첫 장소", categoryLabel = "카페·디저트"),
                            PlanDetailPlaceVO(2, memo = "나중", name = "두 번째 장소"),
                        ),
                )
            coEvery { plans.getPlanDetail(42) } returns plan

            val result = useCase(42)

            assertSame(plan, result)
            assertEquals(listOf("첫 장소", "두 번째 장소"), result.places.map { it.name })
        }

    @Test
    fun `조회 실패는 그대로 전파한다`() =
        runTest {
            val failure = IOException("계획 조회 실패")
            coEvery { plans.getPlanDetail(42) } throws failure

            assertSame(failure, runCatching { useCase(42) }.exceptionOrNull())
        }

    @Test
    fun `planId 가 0 이하면 요청 전에 거른다`() =
        runTest {
            val failure = runCatching { useCase(0) }.exceptionOrNull()

            assertTrue(failure is IllegalArgumentException)
        }
}
