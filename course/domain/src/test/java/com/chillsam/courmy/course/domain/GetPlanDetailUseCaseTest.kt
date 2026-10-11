package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlaceDetailVO
import com.chillsam.courmy.course.entity.PlanDetailPlaceVO
import com.chillsam.courmy.course.entity.PlanDetailVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class GetPlanDetailUseCaseTest {
    private val plans: PlanRepository = mockk()
    private val places: PlaceDetailRepository = mockk()
    private val useCase = GetPlanDetailUseCase(plans, GetPlaceDetailUseCase(places))

    @Test
    fun `장소 응답 완료 순서가 달라도 계획 순서와 메모를 유지한다`() =
        runTest {
            val plan =
                PlanDetailVO(
                    planId = 42,
                    title = "산책",
                    sourceCourseId = 12,
                    places = listOf(PlanDetailPlaceVO(1, memo = "먼저"), PlanDetailPlaceVO(2, memo = "나중")),
                )
            coEvery { plans.getPlanDetail(42) } returns plan
            coEvery { places.getPlaceDetail(1) } coAnswers {
                delay(100)
                PlaceDetailVO(name = "첫 장소")
            }
            coEvery { places.getPlaceDetail(2) } returns PlaceDetailVO(name = "두 번째 장소")

            val result = useCase(42)

            assertEquals(listOf(1L, 2L), result.places.map { it.placeId })
            assertEquals(listOf("첫 장소", "두 번째 장소"), result.places.map { it.detail?.name })
            assertEquals(listOf("먼저", "나중"), result.places.map { it.memo })
            assertEquals(12L, result.sourceCourseId)
        }

    @Test
    fun `장소 하나의 조회 실패는 해당 상세만 비우고 계획을 유지한다`() =
        runTest {
            coEvery { plans.getPlanDetail(42) } returns
                PlanDetailVO(42, "산책", places = listOf(PlanDetailPlaceVO(1), PlanDetailPlaceVO(2)))
            coEvery { places.getPlaceDetail(1) } throws IOException("장소 없음")
            coEvery { places.getPlaceDetail(2) } returns PlaceDetailVO(name = "정상 장소")

            val result = useCase(42)

            assertNull(result.places.first().detail)
            assertEquals(
                "정상 장소",
                result.places
                    .last()
                    .detail
                    ?.name,
            )
        }

    @Test
    fun `장소 조회 취소는 실패 폴백으로 삼키지 않는다`() =
        runTest {
            coEvery { plans.getPlanDetail(42) } returns
                PlanDetailVO(42, "산책", places = listOf(PlanDetailPlaceVO(1)))
            coEvery { places.getPlaceDetail(1) } throws CancellationException("화면 이탈")

            val failure = runCatching { useCase(42) }.exceptionOrNull()

            assertTrue(failure is CancellationException)
        }

    @Test
    fun `계획 자체 조회 실패는 그대로 전파하고 장소를 요청하지 않는다`() =
        runTest {
            val failure = IOException("계획 조회 실패")
            coEvery { plans.getPlanDetail(42) } throws failure

            assertSame(failure, runCatching { useCase(42) }.exceptionOrNull())
            coVerify(exactly = 0) { places.getPlaceDetail(any()) }
        }
}
