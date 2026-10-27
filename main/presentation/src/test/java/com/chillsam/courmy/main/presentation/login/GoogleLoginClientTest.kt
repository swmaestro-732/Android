package com.chillsam.courmy.main.presentation.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.resume

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleLoginClientTest {
    private val context = mockk<Context>()
    private val credentialManager = mockk<CredentialManager>()
    private var originalClientId = ""

    @Before
    fun setUp() {
        originalClientId = GoogleLoginClient.serverClientId
        GoogleLoginClient.serverClientId = "test-server-client-id"
        mockkObject(CredentialManager.Companion)
        every { CredentialManager.create(context) } returns credentialManager
    }

    @After
    fun tearDown() {
        GoogleLoginClient.serverClientId = originalClientId
        unmockkObject(CredentialManager.Companion)
    }

    @Test
    fun buttonRequestWaitsBeyondTwoMinutesAndIgnoresResultAfterCancellation() =
        runTest {
            val request = slot<GetCredentialRequest>()
            lateinit var pending: CancellableContinuation<GetCredentialResponse>
            coEvery { credentialManager.getCredential(context, capture(request)) } coAnswers {
                suspendCancellableCoroutine { pending = it }
            }
            val login = async { runCatching { GoogleLoginClient.login(context) } }
            runCurrent()

            val option = request.captured.credentialOptions.single()
            assertTrue(option is GetSignInWithGoogleOption)
            assertEquals("test-server-client-id", (option as GetSignInWithGoogleOption).serverClientId)

            advanceTimeBy(180_000L)
            runCurrent()
            assertFalse(login.isCompleted)
            assertTrue(pending.isActive)
            login.cancel()
            runCurrent()

            assertTrue(login.isCancelled)
            assertTrue(pending.isCancelled)
            pending.resume(mockk<GetCredentialResponse>())
            runCurrent()
            assertTrue(login.isCancelled)
        }

    @Test
    fun parentTimeoutIsNotConvertedToGoogleLoginTimeout() =
        runTest {
            lateinit var pending: CancellableContinuation<GetCredentialResponse>
            coEvery { credentialManager.getCredential(context, any<GetCredentialRequest>()) } coAnswers {
                suspendCancellableCoroutine { pending = it }
            }

            val result =
                withTimeoutOrNull(1_000L) {
                    GoogleLoginClient.login(context)
                }

            assertNull(result)
            assertTrue(pending.isCancelled)
        }
}
