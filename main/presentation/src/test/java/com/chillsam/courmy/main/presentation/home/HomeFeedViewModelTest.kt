package com.chillsam.courmy.main.presentation.home

import android.util.Log
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.main.domain.home.GetHomeFeedUseCase
import com.chillsam.courmy.main.domain.saved.GetSavedCourseIdsUseCase
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFeedViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getFeed = mockk<GetHomeFeedUseCase>()
    private val setSaved = mockk<SetCourseSavedUseCase>()
    private val getSavedIds = mockk<GetSavedCourseIdsUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any<Throwable>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0
        coEvery { setSaved(any(), any()) } returns Unit
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `저장 응답 전에는 집계를 유지하고 연타해도 성공 후 한 번만 증가한다`() =
        runTest(dispatcher) {
            val viewModel = loadedViewModel(course("1", 999, "999"), course("2", 7, "7"))
            advanceUntilIdle()
            val pending = CompletableDeferred<Unit>()
            coEvery { setSaved(1L, true) } coAnswers { pending.await() }

            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            runCurrent()

            assertEquals(999, viewModel.course("1").saveCount)
            assertEquals("999", viewModel.course("1").saveCountText)
            assertFalse("1" in viewModel.uiState.value.savedCourseIds)
            assertTrue("1" in viewModel.uiState.value.savingCourseIds)
            coVerify(exactly = 1) { setSaved(1L, true) }

            pending.complete(Unit)
            advanceUntilIdle()

            assertEquals(1000, viewModel.course("1").saveCount)
            assertEquals("1k", viewModel.course("1").saveCountText)
            assertEquals(7, viewModel.course("2").saveCount)
            assertEquals(setOf("1"), viewModel.uiState.value.savedCourseIds)
            assertTrue(
                viewModel.uiState.value.savingCourseIds
                    .isEmpty(),
            )
        }

    @Test
    fun `저장 취소 성공은 원시 집계를 감소시켜 축약 표시도 갱신한다`() =
        runTest(dispatcher) {
            val viewModel = loadedViewModel(course("1", 1000, "1k"), savedIds = setOf("1"))
            advanceUntilIdle()

            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            advanceUntilIdle()

            assertEquals(999, viewModel.course("1").saveCount)
            assertEquals("999", viewModel.course("1").saveCountText)
            assertFalse("1" in viewModel.uiState.value.savedCourseIds)
            coVerify(exactly = 1) { setSaved(1L, false) }
        }

    @Test
    fun `저장 취소로 집계가 음수가 되지 않는다`() =
        runTest(dispatcher) {
            val viewModel = loadedViewModel(course("1", 0, "0"), savedIds = setOf("1"))
            advanceUntilIdle()

            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            advanceUntilIdle()

            assertEquals(0, viewModel.course("1").saveCount)
            assertEquals("0", viewModel.course("1").saveCountText)
            assertFalse("1" in viewModel.uiState.value.savedCourseIds)
        }

    @Test
    fun `저장 실패는 집계와 저장 상태를 유지하고 재시도할 수 있다`() =
        runTest(dispatcher) {
            val original = course("1", 12, "12")
            val viewModel = loadedViewModel(original)
            advanceUntilIdle()
            coEvery { setSaved(1L, true) } throws IOException("offline")

            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            advanceUntilIdle()

            assertEquals(original, viewModel.course("1"))
            assertTrue(
                viewModel.uiState.value.savedCourseIds
                    .isEmpty(),
            )
            assertTrue(
                viewModel.uiState.value.savingCourseIds
                    .isEmpty(),
            )
            assertEquals("저장하지 못했어요.", viewModel.uiState.value.actionErrorMessage)

            coEvery { setSaved(1L, true) } returns Unit
            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            advanceUntilIdle()
            assertEquals(13, viewModel.course("1").saveCount)
            coVerify(exactly = 2) { setSaved(1L, true) }
        }

    @Test
    fun `복귀 동기화는 저장 상태가 달라진 카드만 보정하고 반복해도 집계가 누적되지 않는다`() =
        runTest(dispatcher) {
            val viewModel =
                loadedViewModel(
                    course("1", 999, "999"),
                    course("2", 1000, "1k"),
                    course("3", 9, "9"),
                    savedIds = setOf("2", "3"),
                )
            advanceUntilIdle()
            viewModel.onIntent(HomeFeedIntent.SelectCategory("데이트"))
            coEvery { getSavedIds() } returns setOf("1", "3", "99")

            repeat(2) {
                viewModel.onIntent(HomeFeedIntent.SyncSavedIds)
                advanceUntilIdle()
            }

            assertEquals(1000, viewModel.course("1").saveCount)
            assertEquals("1k", viewModel.course("1").saveCountText)
            assertEquals(999, viewModel.course("2").saveCount)
            assertEquals("999", viewModel.course("2").saveCountText)
            assertEquals(9, viewModel.course("3").saveCount)
            assertEquals(
                listOf("1", "2", "3"),
                viewModel.uiState.value.courses
                    .map { it.id },
            )
            assertEquals(setOf("1", "3", "99"), viewModel.uiState.value.savedCourseIds)
            assertEquals("데이트", viewModel.uiState.value.selectedCategory)
            assertEquals("next", viewModel.uiState.value.nextCursor)
            assertTrue(viewModel.uiState.value.hasNext)
            coVerify(exactly = 1) { getFeed(any(), any()) }
        }

    @Test
    fun `같은 저장 결과를 동기화해도 이중 증가하지 않고 미제공 집계는 추정하지 않는다`() =
        runTest(dispatcher) {
            val unknown = course("2", null, "")
            val viewModel = loadedViewModel(course("1", 4, "4"), unknown)
            advanceUntilIdle()

            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            viewModel.onIntent(HomeFeedIntent.ToggleSave("2"))
            advanceUntilIdle()
            coEvery { getSavedIds() } returns setOf("1", "2")
            viewModel.onIntent(HomeFeedIntent.SyncSavedIds)
            advanceUntilIdle()

            assertEquals(5, viewModel.course("1").saveCount)
            assertEquals("5", viewModel.course("1").saveCountText)
            assertNull(viewModel.course("2").saveCount)
            assertEquals(unknown, viewModel.course("2"))
            assertEquals(setOf("1", "2"), viewModel.uiState.value.savedCourseIds)
        }

    @Test
    fun `초기 저장 ID 조회 실패 후 동기화 성공은 기존 서버 집계를 증가시키지 않는다`() =
        runTest(dispatcher) {
            val original = course("1", 12, "12")
            val viewModel = loadedViewModel(original)
            coEvery { getSavedIds() } throws IOException("offline")
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.savedIdsKnown)
            assertEquals(original, viewModel.course("1"))
            coEvery { getSavedIds() } returns setOf("1")

            repeat(2) {
                viewModel.onIntent(HomeFeedIntent.SyncSavedIds)
                advanceUntilIdle()
            }

            assertTrue(viewModel.uiState.value.savedIdsKnown)
            assertEquals(setOf("1"), viewModel.uiState.value.savedCourseIds)
            assertEquals(original, viewModel.course("1"))
            coVerify(exactly = 1) { getFeed(any(), any()) }
        }

    @Test
    fun `저장 성공 이후 늦게 끝난 이전 동기화가 저장 상태와 집계를 되돌리지 않는다`() =
        runTest(dispatcher) {
            val viewModel = loadedViewModel(course("1", 12, "12"))
            advanceUntilIdle()
            val pendingSync = CompletableDeferred<Set<String>>()
            val pendingSave = CompletableDeferred<Unit>()
            coEvery { getSavedIds() } coAnswers { pendingSync.await() }
            coEvery { setSaved(1L, true) } coAnswers { pendingSave.await() }
            viewModel.onIntent(HomeFeedIntent.SyncSavedIds)
            runCurrent()

            viewModel.onIntent(HomeFeedIntent.ToggleSave("1"))
            runCurrent()
            viewModel.onIntent(HomeFeedIntent.SyncSavedIds)
            runCurrent()
            // 최초 로드와 저장 전 동기화만 조회하고, 저장 진행 중 추가 조회는 하지 않는다.
            coVerify(exactly = 2) { getSavedIds() }

            pendingSave.complete(Unit)
            advanceUntilIdle()
            assertEquals(setOf("1"), viewModel.uiState.value.savedCourseIds)
            assertEquals(13, viewModel.course("1").saveCount)

            pendingSync.complete(emptySet())
            advanceUntilIdle()

            assertEquals(setOf("1"), viewModel.uiState.value.savedCourseIds)
            assertEquals(13, viewModel.course("1").saveCount)
            assertEquals("13", viewModel.course("1").saveCountText)
            coVerify(exactly = 1) { setSaved(1L, true) }
        }

    private fun loadedViewModel(
        vararg courses: HomeCourseVO,
        savedIds: Set<String> = emptySet(),
    ): HomeFeedViewModel {
        coEvery { getFeed(any(), any()) } returns CursorPageVO(courses.toList(), "next", true)
        coEvery { getSavedIds() } returns savedIds
        return HomeFeedViewModel(getFeed, setSaved, getSavedIds).also { it.onIntent(HomeFeedIntent.Load) }
    }

    private fun course(
        id: String,
        count: Int?,
        text: String,
    ) = HomeCourseVO(id = id, title = "코스 $id", saveCount = count, saveCountText = text)

    private fun HomeFeedViewModel.course(id: String) = uiState.value.courses.single { it.id == id }
}
