package com.chillsam.courmy.common.data.push

import android.app.NotificationManager
import android.content.Context
import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.data.network.appBuildNumber
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class DeviceRegistrationTest {
    private val api = mockk<DeviceApi>()
    private val tokens = mockk<TokenStore>()
    private val fcm = mockk<FcmTokenClient>()
    private val preferences = mockk<PushPreferences>()
    private val preferenceEnabled = AtomicBoolean(true)
    private val telemetry = mockk<Telemetry>(relaxed = true)
    private val context = mockk<Context>()
    private val notifications = mockk<NotificationManager>(relaxed = true)
    private val loaded = AtomicBoolean(true)
    private val user = AtomicLong(1L)
    private val consentDecidedState = AtomicBoolean(false)
    private val consentSyncedState = AtomicBoolean(true)
    private val marketingState = AtomicBoolean(false)
    private val nightState = AtomicBoolean(false)
    private val marketingAt = AtomicReference<String?>(null)
    private val nightAt = AtomicReference<String?>(null)
    private lateinit var devices: DeviceRegistration

    @Before
    fun setUp() {
        mockkStatic("com.chillsam.courmy.common.data.network.AppBuildNumberKt")
        every { context.appBuildNumber() } returns 42L
        every { context.getSystemService(NotificationManager::class.java) } returns notifications
        every { tokens.isLoaded } answers { loaded.get() }
        every { tokens.isLoggedIn } answers { user.get() > 0 }
        every { tokens.userId } answers { user.get().takeIf { it > 0 } }
        coEvery { tokens.load() } coAnswers { loaded.set(true) }
        every { fcm.enableAutoInit() } returns Unit
        every { fcm.disableAutoInit() } returns Unit
        coEvery { fcm.token() } returns "test-token"
        coEvery { fcm.delete() } returns Unit
        coEvery { api.remove(any()) } returns Response.success(DeviceResponse(2000))
        every { preferences.enabled } answers { preferenceEnabled.get() }
        every { preferences.enabled = any() } answers { preferenceEnabled.set(firstArg()) }
        every { preferences.consentDecided } answers { consentDecidedState.get() }
        every { preferences.consentDecided = any() } answers { consentDecidedState.set(firstArg()) }
        every { preferences.consentSynced } answers { consentSyncedState.get() }
        every { preferences.consentSynced = any() } answers { consentSyncedState.set(firstArg()) }
        every { preferences.marketingAgreed } answers { marketingState.get() }
        every { preferences.marketingAgreed = any() } answers { marketingState.set(firstArg()) }
        every { preferences.nightAgreed } answers { nightState.get() }
        every { preferences.nightAgreed = any() } answers { nightState.set(firstArg()) }
        every { preferences.marketingAgreedAt } answers { marketingAt.get() }
        every { preferences.marketingAgreedAt = any() } answers { marketingAt.set(firstArg()) }
        every { preferences.nightAgreedAt } answers { nightAt.get() }
        every { preferences.nightAgreedAt = any() } answers { nightAt.set(firstArg()) }
        devices = DeviceRegistration(api, tokens, fcm, telemetry, preferences, context)
    }

    @After
    fun tearDown() {
        unmockkStatic("com.chillsam.courmy.common.data.network.AppBuildNumberKt")
    }

    @Test
    fun `background token callback restores the saved session before registering`() =
        runBlocking {
            loaded.set(false)
            user.set(0)
            val registered = CompletableDeferred<RegisterDeviceRequest>()
            coEvery { tokens.load() } coAnswers {
                loaded.set(true)
                user.set(7)
            }
            coEvery { api.register(any()) } coAnswers {
                registered.complete(firstArg())
                Response.success(DeviceResponse(2000))
            }

            devices.sync()

            val request = withTimeout(3_000) { registered.await() }
            assertEquals(RegisterDeviceRequest("test-token", "ANDROID", 42), request)
            coVerify(exactly = 1) { tokens.load() }
            devices.unregister(removeRemote = false)
        }

    @Test
    fun `logout waits for in flight registration then removes it without a late registration`() =
        runBlocking {
            val putStarted = CompletableDeferred<Unit>()
            val finishPut = CompletableDeferred<Unit>()
            val operations = mutableListOf<String>()
            coEvery { api.register(any()) } coAnswers {
                operations.add("PUT")
                putStarted.complete(Unit)
                finishPut.await()
                Response.success(DeviceResponse(2000))
            }
            coEvery { api.remove(any()) } coAnswers {
                operations.add("DELETE")
                Response.success(DeviceResponse(2000))
            }
            devices.onSessionAvailable()
            withTimeout(3_000) { putStarted.await() }
            devices.sync()
            val logout = async(start = CoroutineStart.UNDISPATCHED) { devices.unregister() }
            finishPut.complete(Unit)
            withTimeout(3_000) { logout.await() }

            assertEquals(listOf("PUT", "DELETE"), operations)
            coVerify(exactly = 1) { api.register(any()) }
            coVerify(exactly = 1) { api.remove(RemoveDeviceRequest("test-token")) }
            coVerify(exactly = 1) { fcm.delete() }
        }

    @Test
    fun `account change during token lookup discards old registration and registers new session once`() =
        runBlocking {
            val lookupStarted = CompletableDeferred<Unit>()
            val finishLookup = CompletableDeferred<Unit>()
            val registeredFor = CompletableDeferred<Long>()
            coEvery { fcm.token() } coAnswers {
                lookupStarted.complete(Unit)
                finishLookup.await()
                "test-token"
            }
            coEvery { api.register(any()) } coAnswers {
                registeredFor.complete(user.get())
                Response.success(DeviceResponse(2000))
            }
            devices.onSessionAvailable()
            withTimeout(3_000) { lookupStarted.await() }
            user.set(2)
            devices.onSessionAvailable()
            finishLookup.complete(Unit)

            assertEquals(2L, withTimeout(3_000) { registeredFor.await() })
            devices.unregister(removeRemote = false)
            coVerify(exactly = 1) { api.register(any()) }
        }

    @Test
    fun `caller cancellation cannot skip FCM deletion and visible notification cleanup`() =
        runBlocking {
            val deleteStarted = CompletableDeferred<Unit>()
            val finishDelete = CompletableDeferred<Unit>()
            coEvery { api.remove(any()) } coAnswers {
                deleteStarted.complete(Unit)
                finishDelete.await()
                Response.success(DeviceResponse(2000))
            }
            val logout = async(start = CoroutineStart.UNDISPATCHED) { devices.unregister() }
            withTimeout(3_000) { deleteStarted.await() }

            logout.cancel()
            finishDelete.complete(Unit)
            withTimeout(3_000) { logout.join() }

            verify(exactly = 1) { fcm.disableAutoInit() }
            coVerify(exactly = 1) { fcm.delete() }
            verify(exactly = 1) { notifications.cancelAll() }
        }

    @Test
    fun `restore sync during logout cannot enable registration again`() =
        runBlocking {
            val deleteStarted = CompletableDeferred<Unit>()
            val finishDelete = CompletableDeferred<Unit>()
            val queuedSyncEntered = CompletableDeferred<Unit>()
            coEvery { api.remove(any()) } coAnswers {
                deleteStarted.complete(Unit)
                finishDelete.await()
                Response.success(DeviceResponse(2000))
            }
            val logout = async(start = CoroutineStart.UNDISPATCHED) { devices.unregister() }
            withTimeout(3_000) { deleteStarted.await() }
            // JWT is still present until AuthRepository finishes teardown. A restored Activity calls sync.
            every { tokens.isLoaded } answers {
                queuedSyncEntered.complete(Unit)
                true
            }
            devices.sync()
            finishDelete.complete(Unit)
            withTimeout(3_000) {
                logout.await()
                queuedSyncEntered.await()
            }
            // Acquiring the same mutex drains that queued sync before verifying the lack of PUT.
            devices.unregister(removeRemote = false)

            coVerify(exactly = 0) { api.register(any()) }
            verify(exactly = 0) { fcm.enableAutoInit() }
        }

    @Test
    fun `disabled preference blocks restored sessions and token callbacks`() =
        runBlocking {
            devices.setNotificationsEnabled(false)
            val cleanup = CompletableDeferred<Unit>()
            coEvery { fcm.delete() } coAnswers {
                cleanup.complete(Unit)
                Unit
            }
            devices.onSessionAvailable()
            withTimeout(3_000) { cleanup.await() }
            // Mutex를 한 번 더 획득해 앞서 들어온 sync의 종료를 확인한다.
            devices.unregister(removeRemote = false)
            assertEquals(false, preferenceEnabled.get())
            coVerify(exactly = 0) { api.register(any()) }
            verify(exactly = 0) { fcm.enableAutoInit() }
        }

    @Test
    fun `turning notifications back on registers a fresh token`() =
        runBlocking {
            devices.setNotificationsEnabled(false)
            val registered = CompletableDeferred<RegisterDeviceRequest>()
            coEvery { fcm.token() } returns "new-token"
            coEvery { api.register(any()) } coAnswers {
                registered.complete(firstArg())
                Response.success(DeviceResponse(2000))
            }
            devices.setNotificationsEnabled(true)
            assertEquals("new-token", withTimeout(3_000) { registered.await() }.fcmToken)
            assertEquals(true, preferenceEnabled.get())
            devices.unregister(removeRemote = false)
        }

    @Test
    fun `delete failure preserves opt out and reports incomplete suppression`() =
        runBlocking {
            coEvery { fcm.delete() } throws java.io.IOException("offline")
            val failure = runCatching { devices.setNotificationsEnabled(false) }.exceptionOrNull()
            org.junit.Assert.assertTrue(failure is java.io.IOException)
            assertEquals(false, preferenceEnabled.get())
            verify { notifications.cancelAll() }
        }

    @Test
    fun `logout keeps the fcm token and auto init so the device stays a guest`() =
        runBlocking {
            // 로그아웃: 서버 연결 해제는 요청하되 토큰·auto-init 은 남겨 비로그인 기기로 둔다(SCRUM-756).
            devices.unregister(keepToken = true)

            coVerify(exactly = 1) { api.remove(RemoveDeviceRequest("test-token")) }
            coVerify(exactly = 0) { fcm.delete() }
            verify(exactly = 0) { fcm.disableAutoInit() }
            verify(exactly = 1) { notifications.cancelAll() }
        }

    @Test
    fun `consent sync stores the server processed timestamps`() =
        runBlocking {
            // syncConsent 은 marketingAgreedAt 기록 → nightAgreedAt → consentSynced=true 순서로 쓴다.
            // 마지막 단계(consentSynced=true)를 기다려야 앞 시각 기록이 모두 끝난 걸 보장한다(기록 직후 깨어나 검증하면 경쟁).
            val synced = CompletableDeferred<Unit>()
            every { preferences.consentSynced = any() } answers {
                val value = firstArg<Boolean>()
                consentSyncedState.set(value)
                if (value && !synced.isCompleted) synced.complete(Unit)
                Unit
            }
            coEvery { api.updateDevice(any()) } returns
                Response.success(
                    DeviceConsentResponse(
                        code = 2000,
                        data = DeviceConsentDTO(marketingAgreedAt = "2026-11-03T05:00:00Z", nightAgreedAt = null),
                    ),
                )

            devices.save(marketingAgreed = true, nightAgreed = false)

            withTimeout(3_000) { synced.await() }
            assertEquals(true, consentSyncedState.get())
            assertEquals("2026-11-03T05:00:00Z", devices.marketingAgreedAt)
        }

    @Test
    fun `refreshFromServer pulls the current consent into the local cache`() =
        runBlocking {
            consentDecidedState.set(true)
            consentSyncedState.set(true)
            coEvery { api.consent() } returns
                Response.success(
                    DeviceConsentResponse(
                        code = 2000,
                        data =
                            DeviceConsentDTO(
                                marketingAgreedAt = "2026-11-03T05:00:00Z",
                                nightAgreedAt = "2026-11-03T06:00:00Z",
                            ),
                    ),
                )

            devices.refreshFromServer()

            assertEquals(true, marketingState.get())
            assertEquals(true, nightState.get())
            assertEquals("2026-11-03T05:00:00Z", marketingAt.get())
            assertEquals("2026-11-03T06:00:00Z", nightAt.get())
        }

    @Test
    fun `refreshFromServer skips the fetch while a local change is pending`() =
        runBlocking {
            consentDecidedState.set(true)
            consentSyncedState.set(false)

            devices.refreshFromServer()

            coVerify(exactly = 0) { api.consent() }
        }
}
