package com.chillsam.courmy.main.presentation.settings

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {
    private val settings = mockk<NotificationSettings>()
    private var enabled = true

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
            val model = NotificationSettingsViewModel(settings)
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
    fun `FCM timeout restores interaction and shows failure with persisted opt out`() =
        runTest {
            coEvery { settings.setNotificationsEnabled(false) } coAnswers {
                enabled = false
                withTimeout(10) { awaitCancellation() }
            }
            val model = NotificationSettingsViewModel(settings)
            model.onIntent(NotificationSettingsIntent.SetEnabled(false))
            advanceUntilIdle()
            assertFalse(model.uiState.value.enabled)
            assertFalse(model.uiState.value.saving)
            assertTrue(model.uiState.value.error)
        }
}
