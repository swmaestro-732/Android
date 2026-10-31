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

class DeviceRegistrationTest {
    private val api = mockk<DeviceApi>()
    private val tokens = mockk<TokenStore>()
    private val fcm = mockk<FcmTokenClient>()
    private val telemetry = mockk<Telemetry>(relaxed = true)
    private val context = mockk<Context>()
    private val notifications = mockk<NotificationManager>(relaxed = true)
    private val loaded = AtomicBoolean(true)
    private val user = AtomicLong(1L)
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
        devices = DeviceRegistration(api, tokens, fcm, telemetry, context)
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
}
