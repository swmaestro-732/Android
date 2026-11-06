package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.TraceVisitVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * 서버는 사용자당 따라가기 세션 1개만 허용한다 — 다른 코스의 묵은 세션이 남아 있으면
 * (409/4100, [ActiveTraceConflictException]) UseCase 가 그 세션을 빈 기록으로 종료하고
 * 한 번만 재시작하는 흐름을 가드한다.
 */
class StartCourseTraceUseCaseTest {
    @Test
    fun `충돌이 없으면 바로 시작한다`() =
        runTest {
            val repository = FakeTraceRepository(startResults = mutableListOf(Result.success(7L)))
            val useCase = StartCourseTraceUseCase(repository)

            assertEquals(7L, useCase(1L))
            assertEquals(listOf("start"), repository.calls)
        }

    @Test
    fun `다른 코스 세션 충돌이면 기존 세션을 종료하고 재시작한다`() =
        runTest {
            val conflict = ActiveTraceConflictException(activeTraceId = 2L, message = "이미 따라가는 중")
            val repository =
                FakeTraceRepository(
                    startResults = mutableListOf(Result.failure(conflict), Result.success(9L)),
                )
            val useCase = StartCourseTraceUseCase(repository)

            assertEquals(9L, useCase(1L))
            assertEquals(listOf("start", "end", "start"), repository.calls)
            assertEquals(2L, repository.endedTraceId)
            // 묵은 세션은 빈 기록(방문 0·0분·0m)으로 닫는다.
            assertTrue(repository.endedVisits.isEmpty())
            assertEquals(0, repository.endedDurationMinutes)
            assertEquals(0, repository.endedDistanceMeters)
        }

    @Test
    fun `기존 세션 종료가 실패해도 재시작은 시도한다`() =
        runTest {
            val conflict = ActiveTraceConflictException(activeTraceId = 2L, message = "이미 따라가는 중")
            val repository =
                FakeTraceRepository(
                    startResults = mutableListOf(Result.failure(conflict), Result.success(9L)),
                    endFailure = IOException("이미 종료된 따라가기"),
                )
            val useCase = StartCourseTraceUseCase(repository)

            assertEquals(9L, useCase(1L))
            assertEquals(listOf("start", "end", "start"), repository.calls)
        }

    @Test
    fun `기존 traceId 를 모르면 충돌을 그대로 전파한다`() =
        runTest {
            val conflict = ActiveTraceConflictException(activeTraceId = null, message = "이미 따라가는 중")
            val repository = FakeTraceRepository(startResults = mutableListOf(Result.failure(conflict)))
            val useCase = StartCourseTraceUseCase(repository)

            assertSame(conflict, runCatching { useCase(1L) }.exceptionOrNull())
            assertEquals(listOf("start"), repository.calls)
        }

    @Test
    fun `courseId 가 0 이하면 요청 전에 거른다`() =
        runTest {
            val repository = FakeTraceRepository(startResults = mutableListOf())

            val failure = runCatching { StartCourseTraceUseCase(repository)(0L) }.exceptionOrNull()

            assertTrue(failure is IllegalArgumentException)
            assertTrue(repository.calls.isEmpty())
        }

    private class FakeTraceRepository(
        private val startResults: MutableList<Result<Long>>,
        private val endFailure: Exception? = null,
    ) : TraceRepository {
        val calls = mutableListOf<String>()
        var endedTraceId: Long? = null
        var endedVisits: List<TraceVisitVO> = emptyList()
        var endedDurationMinutes: Int? = null
        var endedDistanceMeters: Int? = null

        override suspend fun startTrace(courseId: Long): Long {
            calls += "start"
            check(startResults.isNotEmpty()) { "예상보다 많은 startTrace 호출" }
            return startResults.removeAt(0).getOrThrow()
        }

        override suspend fun checkInPlace(
            traceId: Long,
            visit: TraceVisitVO,
        ) {
            calls += "checkIn"
        }

        override suspend fun endTrace(
            traceId: Long,
            visits: List<TraceVisitVO>,
            durationMinutes: Int,
            distanceMeters: Int,
        ) {
            calls += "end"
            endedTraceId = traceId
            endedVisits = visits
            endedDurationMinutes = durationMinutes
            endedDistanceMeters = distanceMeters
            endFailure?.let { throw it }
        }
    }
}
