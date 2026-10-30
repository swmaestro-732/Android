package com.chillsam.courmy.main.presentation.update

import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import com.chillsam.courmy.main.domain.update.CheckAppVersionUseCase
import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppUpdateViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val required = MutableStateFlow(false)
    private val bus = mockk<AppUpdateEventBus>()
    private val checker = mockk<CheckAppVersionUseCase>()
    private val reminder = mockk<OptionalUpdateReminder>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { bus.updateRequired } returns required
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `새 버전 안내를 닫은 뒤 재진입해도 다시 조회하거나 표시하지 않는다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = false) } returns AppVersionCheckVO.Checked(1L, 2L)
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.showOptionalUpdate)

            viewModel.onIntent(AppUpdateIntent.DismissOptionalUpdate)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.showOptionalUpdate)
            coVerify(exactly = 1) { checker(forceRefresh = false) }
        }

    @Test
    fun `최신 버전이면 안내하지 않는다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = false) } returns AppVersionCheckVO.Checked(2L, 2L)
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.showOptionalUpdate)
        }

    @Test
    fun `Remote Config 조회 실패는 앱 진입을 막지 않는다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = false) } throws IllegalStateException("Remote Config unavailable")
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.showOptionalUpdate)
            assertFalse(viewModel.updateRequired.value)
        }

    @Test
    fun `응답 없는 Remote Config 조회는 시간 제한 후 안내 없이 끝난다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = false) } coAnswers { CompletableDeferred<AppVersionCheckVO>().await() }
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.checked)
            assertFalse(viewModel.uiState.value.showOptionalUpdate)
        }

    @Test
    fun `조회 중 강제 업데이트가 오면 늦은 선택 업데이트 응답을 표시하지 않는다`() =
        runTest(dispatcher) {
            val response = CompletableDeferred<AppVersionCheckVO>()
            coEvery { checker(forceRefresh = false) } coAnswers { response.await() }
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            runCurrent()
            required.value = true
            response.complete(AppVersionCheckVO.Checked(1L, 2L))
            advanceUntilIdle()
            assertTrue(viewModel.updateRequired.value)
            assertFalse(viewModel.uiState.value.showOptionalUpdate)
        }

    @Test
    fun `강제 업데이트 상태에서는 Remote Config 조회를 시작하지 않는다`() =
        runTest(dispatcher) {
            required.value = true
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()
            coVerify(exactly = 0) { checker(forceRefresh = false) }
        }

    @Test
    fun `같은날 다시 실행하면 나중에 선택한 안내는 조회하지 않는다`() =
        runTest(dispatcher) {
            every { reminder.isDismissedToday() } returns true
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.CheckOptionalUpdate)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.showOptionalUpdate)
            coVerify(exactly = 0) { checker(forceRefresh = false) }
        }

    @Test
    fun `나중에는 날짜를 저장하지만 업데이트 선택은 저장하지 않는다`() =
        runTest(dispatcher) {
            val viewModel = AppUpdateViewModel(bus, checker, reminder)
            viewModel.onIntent(AppUpdateIntent.AcceptOptionalUpdate)
            verify(exactly = 0) { reminder.dismissForToday() }
            viewModel.onIntent(AppUpdateIntent.DismissOptionalUpdate)
            verify(exactly = 1) { reminder.dismissForToday() }
        }
}
