package com.chillsam.courmy.main.presentation.login

import android.content.Context
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.main.domain.auth.SocialLoginUseCase
import com.chillsam.courmy.main.entity.auth.SocialLoginResult
import com.chillsam.courmy.main.entity.auth.SocialProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private val context = mockk<Context>()
    private val socialLogin = mockk<SocialLoginUseCase>()
    private val telemetry = mockk<Telemetry>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockkObject(GoogleLoginClient)
    }

    @After
    fun tearDown() {
        unmockkObject(GoogleLoginClient)
        Dispatchers.resetMain()
    }

    @Test
    fun accountSelectionWaitsWithoutSpinnerAndAllowsCancelAndRetry() =
        runTest {
            val token = CompletableDeferred<String>()
            coEvery { GoogleLoginClient.login(context) } coAnswers { token.await() }
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.isSelectingGoogleAccount)
            coVerify(exactly = 0) { socialLogin(any(), any()) }

            advanceTimeBy(180_000L)
            runCurrent()
            assertTrue(viewModel.uiState.value.isSelectingGoogleAccount)
            assertFalse(viewModel.uiState.value.isLoading)
            viewModel.onIntent(LoginIntent.CancelGoogleLogin)
            runCurrent()
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isSelectingGoogleAccount)
            assertNull(viewModel.uiState.value.result)
            coVerify(exactly = 0) { socialLogin(any(), any()) }

            coEvery { GoogleLoginClient.login(context) } returns "retry-token"
            coEvery { socialLogin(SocialProvider.GOOGLE, "retry-token") } returns SocialLoginResult(newUser = false)
            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            assertNull(viewModel.uiState.value.errorMessage)
            runCurrent()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(LoginResult.EXISTING_USER, viewModel.uiState.value.result)
            coVerify(exactly = 1) { socialLogin(SocialProvider.GOOGLE, "retry-token") }
        }

    @Test
    fun coroutineCancellationClearsLoadingWithoutShowingAnError() =
        runTest {
            val token = CompletableDeferred<String>()
            coEvery { GoogleLoginClient.login(context) } coAnswers { token.await() }
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.isSelectingGoogleAccount)
            token.cancel(CancellationException("화면 종료"))
            runCurrent()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
            assertNull(viewModel.uiState.value.result)
            coVerify(exactly = 0) { socialLogin(any(), any()) }
            verify(exactly = 0) { telemetry.recordFailure(any()) }
        }

    @Test
    fun accountPickerDismissalIsNotReportedAsFailure() =
        runTest {
            coEvery { GoogleLoginClient.login(context) } throws
                GoogleLoginClient.CanceledException(IllegalStateException("사용자가 닫음"))
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()

            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
            assertNull(viewModel.uiState.value.result)
            coVerify(exactly = 0) { socialLogin(any(), any()) }
            verify(exactly = 0) { telemetry.recordFailure(any()) }
        }

    @Test
    fun repeatedClicksWhileWaitingOpenOnlyOneAccountPicker() =
        runTest {
            val token = CompletableDeferred<String>()
            coEvery { GoogleLoginClient.login(context) } coAnswers { token.await() }
            coEvery { socialLogin(SocialProvider.GOOGLE, "token") } returns SocialLoginResult(newUser = true)
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()

            assertFalse(viewModel.uiState.value.isLoading)
            assertTrue(viewModel.uiState.value.isSelectingGoogleAccount)
            coVerify(exactly = 1) { GoogleLoginClient.login(context) }
            token.complete("token")
            runCurrent()

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(LoginResult.NEW_USER, viewModel.uiState.value.result)
            coVerify(exactly = 1) { socialLogin(SocialProvider.GOOGLE, "token") }
        }

    @Test
    fun serverRequestShowsSpinnerAndTimesOutAfterThirtySeconds() =
        runTest {
            val response = CompletableDeferred<SocialLoginResult>()
            coEvery { GoogleLoginClient.login(context) } returns "token"
            coEvery { socialLogin(SocialProvider.GOOGLE, "token") } coAnswers { response.await() }
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            assertTrue(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.isSelectingGoogleAccount)
            // 계정 선택 취소 입력이 서버 요청을 중단해서는 안 된다.
            viewModel.onIntent(LoginIntent.CancelGoogleLogin)
            advanceTimeBy(29_999L)
            runCurrent()
            assertTrue(viewModel.uiState.value.isLoading)
            advanceTimeBy(1L)
            runCurrent()

            assertFalse(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.isSelectingGoogleAccount)
            assertEquals("로그인 응답이 늦어지고 있어요. 다시 시도해 주세요.", viewModel.uiState.value.errorMessage)
            assertNull(viewModel.uiState.value.result)

            coEvery { socialLogin(SocialProvider.GOOGLE, "token") } returns SocialLoginResult(newUser = false)
            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
            assertEquals(LoginResult.EXISTING_USER, viewModel.uiState.value.result)
        }

    @Test
    fun cancelBeforeSdkStartsClearsSelectionAndAllowsRetry() =
        runTest {
            coEvery { GoogleLoginClient.login(context) } returns "token"
            coEvery { socialLogin(SocialProvider.GOOGLE, "token") } returns SocialLoginResult(newUser = false)
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            viewModel.onIntent(LoginIntent.CancelGoogleLogin)
            runCurrent()
            assertFalse(viewModel.uiState.value.isSelectingGoogleAccount)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
            coVerify(exactly = 0) { GoogleLoginClient.login(context) }

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            assertEquals(LoginResult.EXISTING_USER, viewModel.uiState.value.result)
        }

    @Test
    fun lateTokenAfterCancellationCannotStartServerLoginOrOverlapRetry() =
        runTest {
            val token = CompletableDeferred<String>()
            coEvery { GoogleLoginClient.login(context) } coAnswers {
                withContext(NonCancellable) { token.await() }
            }
            val viewModel = LoginViewModel(socialLogin, telemetry)

            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            viewModel.onIntent(LoginIntent.CancelGoogleLogin)
            viewModel.onIntent(LoginIntent.GoogleLogin(context))
            runCurrent()
            coVerify(exactly = 1) { GoogleLoginClient.login(context) }

            token.complete("late-token")
            runCurrent()
            coVerify(exactly = 0) { socialLogin(any(), any()) }
            assertFalse(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.isSelectingGoogleAccount)
            assertNull(viewModel.uiState.value.result)
            assertNull(viewModel.uiState.value.errorMessage)
        }
}
