package com.chillsam.courmy.main.presentation.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/**
 * 구글 로그인 진입점. Credential Manager 시트로 계정을 고르면 OIDC **idToken** 을 돌려준다
 * (서버 social-login 은 idToken 을 요구).
 *
 * [serverClientId] 는 클라우드 콘솔의 **"웹 애플리케이션"** OAuth 클라이언트 ID 다 — Android 용이 아니라
 * 서버가 idToken 의 audience 로 검증하는 값이라서다. [CourmyApplication] 이 BuildConfig 로 주입한다.
 */
object GoogleLoginClient {
    /** 사용자가 계정 선택 시트를 닫은 경우. 호출부는 이걸로 "조용히 앱 복귀"를 판단한다. */
    class CanceledException : RuntimeException("구글 로그인이 취소되었습니다.")

    /** 콘솔 키 미설정(개발 중). */
    class NotConfiguredException : IllegalStateException("구글 로그인 키가 설정되지 않았습니다.")

    @Volatile
    var serverClientId: String = ""

    suspend fun login(context: Context): String {
        if (serverClientId.isBlank()) throw NotConfiguredException()
        val option =
            GetGoogleIdOption
                .Builder()
                .setServerClientId(serverClientId)
                // 이 앱에 로그인한 적 없는 계정도 고를 수 있게 한다(첫 로그인 화면이므로).
                .setFilterByAuthorizedAccounts(false)
                .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val credential =
            try {
                CredentialManager.create(context).getCredential(context, request).credential
            } catch (e: GetCredentialCancellationException) {
                throw CanceledException()
            } catch (e: NoCredentialException) {
                throw IllegalStateException("기기에 사용할 수 있는 구글 계정이 없습니다.", e)
            }
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            throw IllegalStateException("예상하지 못한 credential 타입: ${credential.type}")
        }
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }
}
