package com.chillsam.courmy.main.presentation.login

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
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
            val handle: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                when {
                    isUserCancel(error) -> {
                        cont.resumeWithException(CanceledException())
                    }

                    error != null -> {
                        cont.resumeWithException(error)
                    }

                    token?.idToken != null -> {
                        cont.resume(token.idToken!!)
                    }

                    else -> {
                        cont.resumeWithException(
                            IllegalStateException("카카오 idToken 이 없습니다. 개발자콘솔에서 OpenID Connect 를 활성화하세요."),
                        )
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    when {
                        // 취소는 계정 로그인으로 넘기지 않고 그대로 취소 처리(앱으로 복귀).
                        isUserCancel(error) -> cont.resumeWithException(CanceledException())

                        // 카카오톡을 쓸 수 없는 경우(미연동 등)에만 카카오계정 웹 로그인으로 폴백.
                        error != null -> UserApiClient.instance.loginWithKakaoAccount(context, callback = handle)

                        else -> handle(token, null)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = handle)
            }
        }

    /** 카카오 세션 로그아웃(토큰 폐기). 실패해도 앱 로그아웃은 진행하므로 예외를 삼킨다. */
    suspend fun logout() =
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.logout { _ -> cont.resume(Unit) }
        }

    /** 카카오 연결 끊기(회원 탈퇴 시). 다음 로그인에서 동의를 다시 받는다. */
    suspend fun unlink() =
        suspendCancellableCoroutine { cont ->
            UserApiClient.instance.unlink { _ -> cont.resume(Unit) }
        }
}
