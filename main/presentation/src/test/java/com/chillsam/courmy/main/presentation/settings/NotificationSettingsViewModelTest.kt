package com.chillsam.courmy.main.presentation.settings

import com.chillsam.courmy.common.domain.push.NotificationConsentStore
import com.chillsam.courmy.common.domain.push.NotificationSettings
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {
    private val settings = mockk<NotificationSettings>()
    private var enabled = true

    private class FakeConsentStore : NotificationConsentStore {
        override var isDecided: Boolean = true
        override var marketingAgreed: Boolean = false
        override var nightAgreed: Boolean = false
        override var marketingAgreedAt: String? = null
        override var nightAgreedAt: String? = null

        // refreshFromServer 가 반영할 서버 측 값과 호출 횟수.
        var serverMarketingAt: String? = null
        var serverNightAt: String? = null
        var refreshCount = 0

        override suspend fun save(
            marketingAgreed: Boolean,
            nightAgreed: Boolean,
        ) {
            isDecided = true
            this.marketingAgreed = marketingAgreed
            this.nightAgreed = nightAgreed && marketingAgreed
            if (!this.marketingAgreed) marketingAgreedAt = null
            if (!this.nightAgreed) nightAgreedAt = null
        }

        override suspend fun refreshFromServer() {
            refreshCount++
            marketingAgreedAt = serverMarketingAt
            nightAgreedAt = serverNightAt
        }
    }

    private val consent = FakeConsentStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        every { settings.notificationsEnabled } answers { enabled }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saving blocks rapid toggles and refresh until completed`() =
        runTest {
            val finish = CompletableDeferred<Unit>()
            coEvery { settings.setNotificationsEnabled(false) } coAnswers {
                finish.await()
                enabled = false
            }
            val model = NotificationSettingsViewModel(settings, consent)
            model.onIntent(NotificationSettingsIntent.SetEnabled(false))
            runCurrent()
            model.onIntent(NotificationSettingsIntent.SetEnabled(true))
            model.onIntent(NotificationSettingsIntent.Refresh)
            assertTrue(model.uiState.value.saving)
            finish.complete(Unit)
            advanceUntilIdle()
            assertFalse(model.uiState.value.enabled)
            assertFalse(model.uiState.value.saving)
            coVerify(exactly = 0) { settings.setNotificationsEnabled(true) }
        }

    @Test
    fun `marketing withdrawal also withdraws night consent`() =
        runTest {
            consent.marketingAgreed = true
            consent.nightAgreed = true
            val model = NotificationSettingsViewModel(settings, consent)

            model.onIntent(NotificationSettingsIntent.SetMarketing(false))
            advanceUntilIdle()

            assertFalse(consent.marketingAgreed)
            assertFalse(consent.nightAgreed)
            assertFalse(model.uiState.value.marketing)
            assertFalse(model.uiState.value.night)
        }

    @Test
    fun `turning night on first also turns marketing consent on`() =
        runTest {
            val model = NotificationSettingsViewModel(settings, consent)

            model.onIntent(NotificationSettingsIntent.SetNight(true))
            advanceUntilIdle()

            assertTrue(consent.marketingAgreed)
            assertTrue(consent.nightAgreed)
            assertTrue(model.uiState.value.marketing)
            assertTrue(model.uiState.value.night)

            // 야간만 끄면 광고 동의는 유지된다.
            model.onIntent(NotificationSettingsIntent.SetNight(false))
            advanceUntilIdle()

            assertTrue(consent.marketingAgreed)
            assertFalse(consent.nightAgreed)
        }

    @Test
    fun `refresh pulls server consent timestamps for the notice`() =
        runTest {
            consent.marketingAgreed = true
            consent.serverMarketingAt = "2026-11-03T05:00:00Z"
            val model = NotificationSettingsViewModel(settings, consent)

            model.onIntent(NotificationSettingsIntent.Refresh)
            advanceUntilIdle()

            assertEquals(1, consent.refreshCount)
            assertEquals("2026-11-03T05:00:00Z", model.uiState.value.marketingAgreedAt)
        }

    @Test
    fun `FCM timeout restores interaction and shows failure with persisted opt out`() =
        runTest {
            coEvery { settings.setNotificationsEnabled(false) } coAnswers {
                enabled = false
                withTimeout(10) { awaitCancellation() }
            }
            val model = NotificationSettingsViewModel(settings, consent)
            model.onIntent(NotificationSettingsIntent.SetEnabled(false))
            advanceUntilIdle()
            assertFalse(model.uiState.value.enabled)
            assertFalse(model.uiState.value.saving)
            assertTrue(model.uiState.value.error)
        }
}
