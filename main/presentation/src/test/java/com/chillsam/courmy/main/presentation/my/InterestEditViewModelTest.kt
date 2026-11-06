package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.domain.profile.SaveInterestsUseCase
import com.chillsam.courmy.main.entity.my.MyProfileVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class InterestEditViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getProfile = mockk<GetMyProfileUseCase>()
    private val saveInterests = mockk<SaveInterestsUseCase>()
    private val themes = listOf("date", "nature", "cafetour")
    private val profile =
        mockk<MyProfileVO> {
            every { interestThemes } returns themes
            every { interestRegions } returns emptyList()
        }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { getProfile(any(), any()) } returns profile
        coEvery { saveInterests.saveThemes(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `조회 실패는 빈 선택 성공으로 처리하지 않고 재시도로 서버 선택을 복원한다`() =
        runTest(dispatcher) {
            coEvery { getProfile(any(), any()) } throws IOException("offline")
            val viewModel = InterestEditViewModel(getProfile, saveInterests)
            viewModel.onIntent(InterestEditIntent.Load)
            assertTrue(viewModel.uiState.value.isLoading)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.loaded)
            assertTrue(viewModel.uiState.value.loadError)
            viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
            advanceUntilIdle()
            coVerify(exactly = 0) { saveInterests.saveThemes(any()) }
            coEvery { getProfile(any(), any()) } returns profile
            viewModel.onIntent(InterestEditIntent.Load)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.loaded)
            assertFalse(viewModel.uiState.value.loadError)
            assertEquals(themes, viewModel.uiState.value.themes)
        }

    @Test
    fun `저장 실패는 화면을 닫지 않고 재시도 성공 이후에만 완료된다`() =
        runTest(dispatcher) {
            val viewModel = InterestEditViewModel(getProfile, saveInterests)
            viewModel.onIntent(InterestEditIntent.Load)
            advanceUntilIdle()
            coEvery { saveInterests.saveThemes(any()) } throws IOException("offline")
            viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.saveError)
            assertFalse(viewModel.uiState.value.saved)
            assertFalse(viewModel.uiState.value.isSaving)
            assertEquals(themes, viewModel.uiState.value.themes)
            coEvery { saveInterests.saveThemes(any()) } returns Unit
            viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.saved)
            assertFalse(viewModel.uiState.value.saveError)
        }

    @Test
    fun `조회와 저장 연속 입력은 중복 요청을 만들지 않는다`() =
        runTest(dispatcher) {
            val viewModel = InterestEditViewModel(getProfile, saveInterests)
            viewModel.onIntent(InterestEditIntent.Load)
            viewModel.onIntent(InterestEditIntent.Load)
            advanceUntilIdle()
            coVerify(exactly = 1) { getProfile(any(), any()) }
            val pending = CompletableDeferred<Unit>()
            coEvery { saveInterests.saveThemes(any()) } coAnswers { pending.await() }
            viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
            viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
            runCurrent()
            assertTrue(viewModel.uiState.value.isSaving)
            coVerify(exactly = 1) { saveInterests.saveThemes(themes) }
            pending.complete(Unit)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.saved)
        }
}
