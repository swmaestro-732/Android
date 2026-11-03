package com.chillsam.courmy.main.presentation.notification

import com.chillsam.courmy.common.domain.push.NotificationConsentStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationConsentViewModelTest {
    private class FakeConsentStore(
        decided: Boolean = false,
    ) : NotificationConsentStore {
        override var isDecided: Boolean = decided
        override var marketingAgreed: Boolean = false
        override var nightAgreed: Boolean = false
        var saveCount = 0

        override suspend fun save(
            marketingAgreed: Boolean,
            nightAgreed: Boolean,
        ) {
            saveCount++
            isDecided = true
            this.marketingAgreed = marketingAgreed
            this.nightAgreed = nightAgreed && marketingAgreed
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sheet shows only until the user has decided`() {
        assertTrue(NotificationConsentViewModel(FakeConsentStore(decided = false)).uiState.value.visible)
        assertFalse(NotificationConsentViewModel(FakeConsentStore(decided = true)).uiState.value.visible)
    }

    @Test
    fun `night consent requires marketing consent`() {
        val model = NotificationConsentViewModel(FakeConsentStore())

        // 광고성 동의 전에는 야간 토글이 무시된다.
        model.onIntent(NotificationConsentIntent.ToggleNight)
        assertFalse(model.uiState.value.nightAgreed)

        model.onIntent(NotificationConsentIntent.ToggleMarketing)
        model.onIntent(NotificationConsentIntent.ToggleNight)
        assertTrue(model.uiState.value.nightAgreed)

        // 광고성 동의를 철회하면 야간 동의도 함께 철회된다.
        model.onIntent(NotificationConsentIntent.ToggleMarketing)
        assertFalse(model.uiState.value.marketingAgreed)
        assertFalse(model.uiState.value.nightAgreed)
    }

    @Test
    fun `confirm is ignored while the required service consent is off`() =
        runTest {
            val store = FakeConsentStore()
            val model = NotificationConsentViewModel(store)
            model.onIntent(NotificationConsentIntent.ToggleService)

            model.onIntent(NotificationConsentIntent.Confirm)
            advanceUntilIdle()

            assertEquals(0, store.saveCount)
            assertTrue(model.uiState.value.visible)
        }

    @Test
    fun `confirm with only the required consent records the decision`() =
        runTest {
            val store = FakeConsentStore()
            val model = NotificationConsentViewModel(store)

            model.onIntent(NotificationConsentIntent.Confirm)
            advanceUntilIdle()

            assertEquals(1, store.saveCount)
            assertTrue(store.isDecided)
            assertFalse(store.marketingAgreed)
            assertFalse(store.nightAgreed)
            assertFalse(model.uiState.value.visible)
        }

    @Test
    fun `confirm saves the selection once and hides the sheet`() =
        runTest {
            val store = FakeConsentStore()
            val model = NotificationConsentViewModel(store)
            model.onIntent(NotificationConsentIntent.ToggleMarketing)

            model.onIntent(NotificationConsentIntent.Confirm)
            // 저장이 끝나기 전의 중복 확인은 무시된다.
            model.onIntent(NotificationConsentIntent.Confirm)
            advanceUntilIdle()

            assertEquals(1, store.saveCount)
            assertTrue(store.isDecided)
            assertTrue(store.marketingAgreed)
            assertFalse(store.nightAgreed)
            assertFalse(model.uiState.value.visible)
        }
}
