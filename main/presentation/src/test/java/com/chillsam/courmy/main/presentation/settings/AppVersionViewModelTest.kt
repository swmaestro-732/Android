package com.chillsam.courmy.main.presentation.settings

import com.chillsam.courmy.main.domain.update.CheckAppVersionUseCase
import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppVersionViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val checker = mockk<CheckAppVersionUseCase>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ViewModel 생성만으로 조회하지 않고 화면의 진입 신호를 기다린다`() =
        runTest(dispatcher) {
            val viewModel = AppVersionViewModel(checker)
            advanceUntilIdle()

            assertEquals(AppVersionStatus.Loading, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.latestVersion)
            coVerify(exactly = 0) { checker(any()) }
        }

    @Test
    fun `첫 진입 조회 중 연속 재확인을 무시하고 최신 상태로 전환한다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<AppVersionCheckVO>()
            coEvery { checker(forceRefresh = true) } coAnswers { pending.await() }
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            runCurrent()
            assertEquals(AppVersionStatus.Loading, viewModel.uiState.value.status)
            viewModel.onIntent(AppVersionIntent.Check)
            pending.complete(AppVersionCheckVO.Checked(12, 12, "1.0.0"))
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Latest, viewModel.uiState.value.status)
            assertEquals("1.0.0", viewModel.uiState.value.latestVersion)
            coVerify(exactly = 1) { checker(forceRefresh = true) }
            coVerify(exactly = 0) { checker(forceRefresh = false) }
        }

    @Test
    fun `새 버전이 있으면 업데이트 가능으로 표시한다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Checked(12, 13, "1.0.1")
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.UpdateAvailable, viewModel.uiState.value.status)
            assertEquals("1.0.1", viewModel.uiState.value.latestVersion)
        }

    @Test
    fun `설정 미제공은 최신으로 표시하지 않고 재확인으로 복구한다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Unavailable
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Unavailable, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.latestVersion)
            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Checked(12, 13, "1.0.1")
            viewModel.onIntent(AppVersionIntent.Check)
            assertEquals(AppVersionStatus.Loading, viewModel.uiState.value.status)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.UpdateAvailable, viewModel.uiState.value.status)
            assertEquals("1.0.1", viewModel.uiState.value.latestVersion)
            coVerify(exactly = 2) { checker(forceRefresh = true) }
        }

    @Test
    fun `조회 예외는 실패 상태로 표시한다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } throws IllegalStateException("offline")
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Unavailable, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.latestVersion)
        }

    @Test
    fun `응답이 없으면 시간 제한 후 재확인할 수 있다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } coAnswers { CompletableDeferred<AppVersionCheckVO>().await() }
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Unavailable, viewModel.uiState.value.status)
            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Checked(13, 12)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Latest, viewModel.uiState.value.status)
            assertEquals("빌드 12", viewModel.uiState.value.latestVersion)
        }

    @Test
    fun `화면 진입과 재진입은 각각 강제 조회하여 바뀐 최신 이름과 상태를 표시한다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } returnsMany
                listOf(AppVersionCheckVO.Checked(12, 12, "1.0.0"), AppVersionCheckVO.Checked(12, 15, "1.0.3"))
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals("1.0.0", viewModel.uiState.value.latestVersion)

            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()

            assertEquals(AppVersionStatus.UpdateAvailable, viewModel.uiState.value.status)
            assertEquals("1.0.3", viewModel.uiState.value.latestVersion)
            coVerify(exactly = 2) { checker(forceRefresh = true) }
            coVerify(exactly = 0) { checker(forceRefresh = false) }
        }

    @Test
    fun `원격 이름이 공백이면 최신 버전 코드를 표시한다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Checked(12, 15, "  ")
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals("빌드 15", viewModel.uiState.value.latestVersion)
        }

    @Test
    fun `새로고침 실패나 시간 초과에는 이전 최신 버전 이름이 남지 않는다`() =
        runTest(dispatcher) {
            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Checked(12, 13, "1.0.1")
            val viewModel = AppVersionViewModel(checker)
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals("1.0.1", viewModel.uiState.value.latestVersion)

            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Unavailable
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Unavailable, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.latestVersion)

            coEvery { checker(forceRefresh = true) } returns AppVersionCheckVO.Checked(12, 15, "1.0.3")
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals("1.0.3", viewModel.uiState.value.latestVersion)

            coEvery { checker(forceRefresh = true) } coAnswers { CompletableDeferred<AppVersionCheckVO>().await() }
            viewModel.onIntent(AppVersionIntent.Check)
            advanceUntilIdle()
            assertEquals(AppVersionStatus.Unavailable, viewModel.uiState.value.status)
            assertNull(viewModel.uiState.value.latestVersion)
        }
}
