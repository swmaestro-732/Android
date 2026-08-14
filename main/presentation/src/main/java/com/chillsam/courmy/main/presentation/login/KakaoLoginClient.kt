package com.chillsam.courmy.main.presentation.login

import android.content.Context
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 카카오 로그인 진입점. 카카오톡 앱이 있으면 앱 로그인, 없으면 카카오계정 웹 로그인으로 OIDC **idToken** 을
 * 돌려준다(서버 social-login 은 idToken 을 요구). idToken 은 개발자콘솔에서 OpenID Connect 활성화 시 내려온다.
 */
object KakaoLoginClient {
    /** 사용자가 로그인을 취소한 경우. 호출부는 이걸로 "조용히 앱 복귀"를 판단한다. */
    class CanceledException : RuntimeException("카카오 로그인이 취소되었습니다.")

    /**
     * 사용자의 명시적 취소인지 판별한다.
     * - 카카오톡 동의 화면 뒤로가기: [ClientErrorCause.Cancelled]
     * - 동의 화면에서 "취소"/거부: [AuthErrorCause.AccessDenied]
     * 둘 다 취소로 보고, 계정 웹 로그인으로 폴백하지 않고 앱으로 복귀한다.
     */
    private fun isUserCancel(error: Throwable?): Boolean =
        (error is ClientError && error.reason == ClientErrorCause.Cancelled) ||
            (error is AuthError && error.reason == AuthErrorCause.AccessDenied)

    suspend fun login(context: Context): String =
        suspendCancellableCoroutine { cont ->
            // continuation 은 한 번만 resume 할 수 있는데, 여기 콜백은 두 번 이상 불릴 수 있다
            // (톡 로그인 실패 → 계정 로그인 폴백 구간에서 양쪽 콜백이 모두 도착하는 경우).
            // 게다가 카카오톡을 다녀오는 사이 호출부가 취소되면 continuation 은 이미 죽어 있다.
            //
            // 가드 없이 resume 하면 두 번째 호출에서 IllegalStateException("Already resumed") 이
            // **SDK 콜백 스레드에서** uncaught 로 터진다. 호출부의 runCatching 은 다른 코루틴이라
            // 이걸 잡지 못하고 프로세스가 죽는다. 그래서 살아 있을 때만 넘긴다.
            fun succeed(idToken: String) {
                if (cont.isActive) cont.resume(idToken)
            }

            fun fail(error: Throwable) {
                if (cont.isActive) cont.resumeWithException(error)
            }

            val handle: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                val idToken = token?.idToken
                when {
                    isUserCancel(error) -> {
                        fail(CanceledException())
                    }

                    error != null -> {
                        fail(error)
                    }

                    idToken != null -> {
                        succeed(idToken)
                    }

                    else -> {
                        fail(
                            IllegalStateException("카카오 idToken 이 없습니다. 개발자콘솔에서 OpenID Connect 를 활성화하세요."),
                        )
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    when {
                        // 취소는 계정 로그인으로 넘기지 않고 그대로 취소 처리(앱으로 복귀).
                        isUserCancel(error) -> fail(CanceledException())

                        // 카카오톡을 쓸 수 없는 경우(미연동 등)에만 카카오계정 웹 로그인으로 폴백.
                        error != null -> UserApiClient.instance.loginWithKakaoAccount(context, callback = handle)

                        else -> handle(token, null)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = handle)
            }
        }

    /**
     * 로그인 실패 리포트에 함께 남길 진단 값.
     *
     * **키 해시는 개인정보가 아니다** — 빌드 서명에서 나오는 값이라 같은 서명의 모든 사용자가 동일하다.
     *
     * 이걸 남기는 이유: Play App Signing 은 APK 에 서명자를 여러 개(V3.0 / V3.2 classical / V3.2 PQC)
     * 넣고, 카카오 SDK 가 읽는 [android.content.pm.PackageInfo.signatures] 는 기기 Android 버전에 따라
     * **서로 다른 인증서**를 돌려준다. 그래서 "일부 기기에서만 keyHash validation 실패" 가 생기는데,
     * 콘솔에 무엇을 더 등록해야 하는지는 **실패한 그 기기가 실제로 보낸 값**을 봐야만 알 수 있다.
     * 이게 없으면 테스터 폰마다 USB 를 꽂아 logcat 을 보는 수밖에 없다.
     */
    fun diagnostics(context: Context): String =
        runCatching {
            "keyHash=${Utility.getKeyHash(context)} " +
                "talk=${UserApiClient.instance.isKakaoTalkLoginAvailable(context)}"
        }.getOrElse { "diagnostics unavailable" }

    /**
     * 토큰 없이 인증 API 를 부르면 안 되는 이유.
     *
     * 토큰이 없으면 [com.kakao.sdk.auth.network.AccessTokenInterceptor] 가
     * `ClientError(TokenNotFound)` 를 던진다. 이건 `RuntimeException` 이지 `IOException` 이 아니라서
     * OkHttp `AsyncCall.run` 이 `catch (t: Throwable)` 로 받아 콜백에 실패를 전달한 뒤 `throw t` 로
     * 되던진다. 되던져진 예외는 OkHttp 워커 스레드 밖으로 나가 uncaught 로 처리되고 프로세스가 죽는다.
     * 호출부의 `runCatching` 은 다른 스레드라 이걸 잡지 못한다. 그래서 호출 전에 막는다.
     */
    private fun hasKakaoToken(): Boolean = AuthApiClient.instance.hasToken()

    /** 카카오 세션 로그아웃(토큰 폐기). 실패해도 앱 로그아웃은 진행하므로 예외를 삼킨다. */
    suspend fun logout() {
        if (!hasKakaoToken()) return
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.logout { _ -> cont.resume(Unit) }
        }
    }

    /** 카카오 연결 끊기(회원 탈퇴 시). 다음 로그인에서 동의를 다시 받는다. */
    suspend fun unlink() {
        if (!hasKakaoToken()) return
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.unlink { _ -> cont.resume(Unit) }
        }
    }
}
