package com.chillsam.courmy.main.data.auth

import com.chillsam.courmy.common.data.auth.TokenStore
import com.chillsam.courmy.common.data.push.DeviceRegistration
import com.chillsam.courmy.main.data.auth.dto.SimpleEnvelope
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryCancellationTest {
    private val source = mockk<AuthDataSource>()
    private val tokens = mockk<TokenStore>(relaxed = true)
    private val devices = mockk<DeviceRegistration>()
    private val repository = AuthRepositoryImpl(source, tokens, devices)

    @Test
    fun `logout cancellation still clears the local session`() =
        runBlocking {
            coEvery { devices.unregister() } throws CancellationException("test cancellation")

            val failure = runCatching { repository.logout() }.exceptionOrNull()

            assertTrue(failure is CancellationException)
            verify(exactly = 1) { tokens.clear() }
        }

    @Test
    fun `successful withdrawal clears session even if local FCM cleanup is cancelled`() =
        runBlocking {
            coEvery { source.withdraw() } returns SimpleEnvelope(code = 2000)
            coEvery { devices.unregister(removeRemote = false) } throws CancellationException("test cancellation")

            val failure = runCatching { repository.withdraw() }.exceptionOrNull()

            assertTrue(failure is CancellationException)
            verify(exactly = 1) { tokens.clear() }
        }

    @Test
    fun `server withdrawal failure preserves session and push registration`() =
        runBlocking {
            coEvery { source.withdraw() } throws IllegalStateException("server unavailable")

            val failure = runCatching { repository.withdraw() }.exceptionOrNull()

            assertTrue(failure is IllegalStateException)
            verify(exactly = 0) { tokens.clear() }
            coVerify(exactly = 0) { devices.unregister(any()) }
        }
}
