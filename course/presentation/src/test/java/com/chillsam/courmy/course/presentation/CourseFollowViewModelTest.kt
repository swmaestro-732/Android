package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.FlowResult
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.course.domain.CheckInTracePlaceUseCase
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.domain.EndCourseTraceUseCase
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.domain.StartCourseTraceUseCase
import com.chillsam.courmy.course.domain.TraceRepository
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.TraceVisitVO
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Proxy

@OptIn(ExperimentalCoroutinesApi::class)
class CourseFollowViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun arrivalWaitsForStartAndDoneWaitsForCheckInAndEnd() =
        runTest {
            val traces = DelayedTraceRepository()
            val viewModel = createViewModel(traces)
            viewModel.onIntent(CourseFollowIntent.Load(1L))
            advanceUntilIdle()
            viewModel.onIntent(CourseFollowIntent.Start)
            runCurrent()

            viewModel.onIntent(CourseFollowIntent.CheckArrival)
            viewModel.onIntent(CourseFollowIntent.CheckArrival)
            runCurrent()
            assertEquals(listOf("start"), traces.calls)
            assertTrue(viewModel.uiState.value.isRecording)
            assertEquals(0, viewModel.uiState.value.visitedCount)

            traces.start.complete(42L)
            runCurrent()
            assertEquals(listOf("start", "checkIn"), traces.calls)
            assertEquals(CourseFollowPhase.TRACKING, viewModel.uiState.value.phase)

            traces.checkIn.complete(Unit)
            runCurrent()
            assertEquals(listOf("start", "checkIn", "end"), traces.calls)
            assertEquals(CourseFollowPhase.TRACKING, viewModel.uiState.value.phase)
            assertTrue(viewModel.uiState.value.isRecording)
            assertEquals(listOf(10L), traces.endedVisits.map { it.placeId })

            traces.end.complete(Unit)
            advanceUntilIdle()
            assertEquals(CourseFollowPhase.DONE, viewModel.uiState.value.phase)
            assertEquals(1, viewModel.uiState.value.visitedCount)
            assertFalse(viewModel.uiState.value.isRecording)

            viewModel.onIntent(CourseFollowIntent.CheckArrival)
            viewModel.onIntent(CourseFollowIntent.Start)
            advanceUntilIdle()
            assertEquals(listOf("start", "checkIn", "end"), traces.calls)
            assertEquals(1, viewModel.uiState.value.visitedCount)
        }

    private fun createViewModel(traces: TraceRepository): CourseFollowViewModel {
        val detail =
            CourseDetailVO(
                title = "한 장소 코스",
                authorName = "작성자",
                authorHandle = "author",
                placeCountText = "1곳",
                walkText = "",
                followerText = "",
                description = "",
                places =
                    listOf(
                        CourseDetailPlaceVO(
                            placeId = 10L,
                            order = 1,
                            name = "도착 장소",
                            category = "",
                            photoCountText = "",
                            tip = "",
                        ),
                    ),
                rating = "",
                reviewCountText = "",
                reviews = emptyList(),
            )
        // 이 테스트는 상세 조회만 사용한다. 다른 저장소 호출은 즉시 실패시킨다.
        val courses =
            Proxy.newProxyInstance(
                CourseRepository::class.java.classLoader,
                arrayOf(CourseRepository::class.java),
            ) { _, method, _ ->
                check(method.name == "getCourseDetail") { "예상하지 않은 호출: ${method.name}" }
                detail
            } as CourseRepository
        val telemetry =
            object : Telemetry {
                override fun setScreen(name: String) = Unit

                override fun logFlow(
                    flow: AppFlow,
                    result: FlowResult,
                    detail: String?,
                ) = Unit

                override fun recordFailure(failure: AppFailure) = Unit
            }
        return CourseFollowViewModel(
            getCourseDetailUseCase = GetCourseDetailUseCase(courses, telemetry),
            startCourseTrace = StartCourseTraceUseCase(traces),
            checkInTracePlace = CheckInTracePlaceUseCase(traces),
            endCourseTrace = EndCourseTraceUseCase(traces),
        )
    }

    private class DelayedTraceRepository : TraceRepository {
        val start = CompletableDeferred<Long>()
        val checkIn = CompletableDeferred<Unit>()
        val end = CompletableDeferred<Unit>()
        val calls = mutableListOf<String>()
        var endedVisits = emptyList<TraceVisitVO>()

        override suspend fun startTrace(courseId: Long): Long {
            calls += "start"
            return start.await()
        }

        override suspend fun checkInPlace(
            traceId: Long,
            visit: TraceVisitVO,
        ) {
            assertEquals(42L, traceId)
            calls += "checkIn"
            checkIn.await()
        }

        override suspend fun endTrace(
            traceId: Long,
            visits: List<TraceVisitVO>,
            durationMinutes: Int,
            distanceMeters: Int,
        ) {
            assertEquals(42L, traceId)
            calls += "end"
            endedVisits = visits
            end.await()
        }
    }
}
