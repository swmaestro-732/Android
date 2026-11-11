package com.chillsam.courmy.main.presentation.login

import com.chillsam.courmy.main.domain.auth.SignupUseCase
import com.chillsam.courmy.main.entity.auth.SignupProfile
import com.chillsam.courmy.main.presentation.testResources
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
class SignupConsentTest {
    private val signup = mockk<SignupUseCase>()
    private val profile = SignupProfile(nickname = "커미", handle = "courmy_test")

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        SignupSelectionStore.clear()
    }

    @After
    fun tearDown() {
        SignupSelectionStore.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `동의값을 생략하거나 거절하면 가입을 호출하지 않는다`() =
        runTest {
            val viewModel = SignupViewModel(signup, resources = testResources())
            viewModel.onIntent(SignupIntent.Submit(profile))
            runCurrent()
            assertFalse(viewModel.uiState.value.isLoading)
            assertFalse(viewModel.uiState.value.done)
            assertEquals("필수 약관에 동의해 주세요.", viewModel.uiState.value.errorMessage)
            viewModel.onIntent(SignupIntent.Submit(profile, requiredTermsAgreed = false))
            runCurrent()
            coVerify(exactly = 0) { signup(any(), any(), any(), any()) }
        }

    @Test
    fun `동의 거절 후 필수 동의를 마치면 가입이 진행된다`() =
        runTest {
            coEvery { signup(profile, null, emptyList(), emptyList()) } returns true
            val viewModel = SignupViewModel(signup, resources = testResources())
            viewModel.onIntent(SignupIntent.Submit(profile))

            viewModel.onIntent(SignupIntent.Submit(profile, requiredTermsAgreed = true))
            runCurrent()

            coVerify(exactly = 1) { signup(profile, null, emptyList(), emptyList()) }
            assertTrue(viewModel.uiState.value.done)
            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(null, viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `필수 동의 세 가지를 모두 선택한 조합만 통과한다`() {
        repeat(8) { mask ->
            SignupSelectionStore.ageConfirmed = mask and 1 != 0
            SignupSelectionStore.termsAgreed = mask and 2 != 0
            SignupSelectionStore.privacyAgreed = mask and 4 != 0

            assertEquals("동의 조합 $mask", mask == 7, SignupSelectionStore.requiredTermsAgreed)
        }
    }

    @Test
    fun `가입 정보 초기화는 기존 필수 동의를 다음 가입으로 넘기지 않는다`() {
        SignupSelectionStore.ageConfirmed = true
        SignupSelectionStore.termsAgreed = true
        SignupSelectionStore.privacyAgreed = true
        SignupSelectionStore.nickname = "이전 가입"

        SignupSelectionStore.clear()

        assertFalse(SignupSelectionStore.ageConfirmed)
        assertFalse(SignupSelectionStore.termsAgreed)
        assertFalse(SignupSelectionStore.privacyAgreed)
        assertFalse(SignupSelectionStore.requiredTermsAgreed)
        assertEquals("", SignupSelectionStore.nickname)
    }
}
