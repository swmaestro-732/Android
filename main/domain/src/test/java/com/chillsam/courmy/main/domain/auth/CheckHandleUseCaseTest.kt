package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.main.entity.auth.HandleCheckResult
import com.chillsam.courmy.main.entity.auth.SignupProfile
import com.chillsam.courmy.main.entity.auth.SocialLoginResult
import com.chillsam.courmy.main.entity.auth.SocialProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 아이디 판정 규칙 검증. 길이·형식은 서버 왕복 없이 걸러야 하고,
 * 네트워크 실패를 "사용 가능"으로 오해하면 가입 단계에서 중복 아이디가 통과한다.
 */
class CheckHandleUseCaseTest {
    private class FakeAuthRepository(
        private val available: Boolean = true,
        private val throwOnCheck: Boolean = false,
    ) : AuthRepository {
        var checkedHandle: String? = null
            private set

        override suspend fun socialLogin(
            provider: SocialProvider,
            idToken: String,
        ): SocialLoginResult = error("이 테스트에서는 호출되지 않아야 한다")

        override suspend fun signup(profile: SignupProfile) = error("호출되지 않아야 한다")

        override suspend fun restoreSession() = error("호출되지 않아야 한다")

        override val isLoggedIn: Boolean get() = true

        override suspend fun logout() = error("호출되지 않아야 한다")

        override suspend fun withdraw() = error("호출되지 않아야 한다")

        override suspend fun isHandleAvailable(handle: String): Boolean {
            checkedHandle = handle
            if (throwOnCheck) error("network down")
            return available
        }
    }

    @Test
    fun `길이가 3자 미만이면 서버에 묻지 않는다`() =
        runTest {
            val repo = FakeAuthRepository()

            assertEquals(HandleCheckResult.INVALID_LENGTH, CheckHandleUseCase(repo)("ab"))
            assertEquals(null, repo.checkedHandle)
        }

    @Test
    fun `길이가 12자를 넘으면 서버에 묻지 않는다`() =
        runTest {
            val repo = FakeAuthRepository()

            assertEquals(HandleCheckResult.INVALID_LENGTH, CheckHandleUseCase(repo)("abcdefghijklm"))
            assertEquals(null, repo.checkedHandle)
        }

    @Test
    fun `허용하지 않는 문자가 있으면 서버에 묻지 않는다`() =
        runTest {
            val repo = FakeAuthRepository()

            assertEquals(HandleCheckResult.INVALID_FORMAT, CheckHandleUseCase(repo)("Jiho"))
            assertEquals(HandleCheckResult.INVALID_FORMAT, CheckHandleUseCase(repo)("ji-ho"))
            assertEquals(null, repo.checkedHandle)
        }

    @Test
    fun `형식을 통과하면 서버 판정을 그대로 쓴다`() =
        runTest {
            val free = CheckHandleUseCase(FakeAuthRepository(available = true))("jiho_1")
            val taken = CheckHandleUseCase(FakeAuthRepository(available = false))("jiho_1")

            assertEquals(HandleCheckResult.AVAILABLE, free)
            assertEquals(HandleCheckResult.TAKEN, taken)
        }

    /** 네트워크 실패를 AVAILABLE 로 뭉개면 화면이 "사용 가능"으로 오해한다. */
    @Test
    fun `서버 조회가 실패하면 ERROR 로 내린다`() =
        runTest {
            val result = CheckHandleUseCase(FakeAuthRepository(throwOnCheck = true))("jiho_1")

            assertEquals(HandleCheckResult.ERROR, result)
        }
}
