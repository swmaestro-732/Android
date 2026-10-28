package com.chillsam.courmy.main.presentation.login

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.MessageDigest

/**
 * 구글 로그인 진입점. Credential Manager 시트로 계정을 고르면 OIDC **idToken** 을 돌려준다
 * (서버 social-login 은 idToken 을 요구).
 *
 * [serverClientId] 는 클라우드 콘솔의 **"웹 애플리케이션"** OAuth 클라이언트 ID 다 — Android 용이 아니라
 * 서버가 idToken 의 audience 로 검증하는 값이라서다. [CourmyApplication] 이 BuildConfig 로 주입한다.
 */
object GoogleLoginClient {
    /** 사용자가 계정 선택 시트를 닫은 경우. 호출부는 이걸로 "조용히 앱 복귀"를 판단한다. */
    class CanceledException(
        cause: Throwable,
    ) : RuntimeException("구글 로그인이 취소되었습니다.", cause)

    /** 콘솔 키 미설정(개발 중). */
    class NotConfiguredException : IllegalStateException("구글 로그인 키가 설정되지 않았습니다.")

    @Volatile
    var serverClientId: String = ""

    suspend fun login(context: Context): String {
        if (serverClientId.isBlank()) throw NotConfiguredException()
        val option =
            GetSignInWithGoogleOption
                .Builder(serverClientId)
                .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val credential = getCredential(context, request)
        check(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL,
        ) { "예상하지 못한 credential 타입: ${credential.type}" }
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    private suspend fun getCredential(
        context: Context,
        request: GetCredentialRequest,
    ): Credential =
        try {
            CredentialManager.create(context).getCredential(context, request).credential
        } catch (e: GetCredentialCancellationException) {
            throw CanceledException(e)
        } catch (e: NoCredentialException) {
            // 계정이 없을 때만이 아니라, 설치본의 서명 SHA-1 이 OAuth 콘솔에 등록되지 않았을 때도
            // 이 예외로 떨어진다(Play 앱 서명 키로 재서명된 출시본에서 그랬다). 단정하지 않는다.
            throw IllegalStateException(
                "구글 계정을 가져오지 못했습니다 — 기기에 계정이 없거나 앱 서명이 콘솔에 미등록.",
                e,
            )
        }

    /**
     * 설치본이 실제로 서명된 인증서의 SHA-1 목록. 실패 리포트에 실어 보낸다.
     *
     * 구글 로그인은 serverClientId 가 속한 클라우드 프로젝트에 **이 SHA-1 로 만든 Android OAuth
     * 클라이언트**가 있어야 동작한다. debug·업로드 키·Play 앱 서명 키가 전부 다르고 Play 는 다중
     * 서명(카카오 키 해시 때 확인)이라, 어떤 서명본이 실패했는지는 기기에서 꺼내 보는 수밖에 없다.
     */
    fun diagnostics(context: Context): String =
        runCatching {
            val signatures =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.packageManager
                        .getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                        .signingInfo
                        ?.apkContentsSigners
                        .orEmpty()
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager
                        .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                        .signatures
                        .orEmpty()
                }
            val sha1s =
                signatures.joinToString { signature ->
                    MessageDigest
                        .getInstance("SHA-1")
                        .digest(signature.toByteArray())
                        .joinToString(":") { "%02X".format(it) }
                }
            "signingSha1=[$sha1s]"
        }.getOrElse { "diagnostics unavailable" }
}
