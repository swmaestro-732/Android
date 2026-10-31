package com.chillsam.courmy.common.data.auth

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenStoreLoadTest {
    @Test
    fun `cancelled storage read stays unloaded so the next launch can retry`() =
        runBlocking {
            val storage = mockk<SecureTokenStorage>()
            val tokens = TokenStore(storage)
            coEvery { storage.read() } throws CancellationException("Activity destroyed during restore")

            val failure = runCatching { tokens.load() }.exceptionOrNull()

            assertTrue(failure is CancellationException)
            assertFalse(tokens.isLoaded)
            coEvery { storage.read() } returns null
            tokens.load()
            assertTrue(tokens.isLoaded)
            coVerify(exactly = 2) { storage.read() }
        }
}
