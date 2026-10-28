package com.chillsam.courmy.main.presentation.login

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 네이버 로그인 진입점. 네이버앱이 있으면 앱 로그인, 없으면 커스텀탭 웹 로그인으로 **accessToken** 을
 * 돌려준다.
 *
 * 네이버는 OIDC(idToken)를 제공하지 않아 서버 social-login 의 `accessToken` 필드로 전송한다.
 */
object NaverLoginClient {
    /** 사용자가 로그인을 취소한 경우. 호출부는 이걸로 "조용히 앱 복귀"를 판단한다. */
    class CanceledException : RuntimeException("네이버 로그인이 취소되었습니다.")

    /** 콘솔 키 미설정으로 SDK 초기화를 건너뛴 경우(개발 중). */
    class NotConfiguredException : IllegalStateException("네이버 로그인 키가 설정되지 않았습니다.")

    /** [CourmyApplication] 이 키가 있을 때만 initialize 후 켠다. */
    @Volatile
    var isConfigured: Boolean = false

    /** 네이버 SDK 가 명시적 취소로 쓰는 에러 코드(`user_cancel`). */
    private const val ERROR_CODE_USER_CANCEL = "user_cancel"

    suspend fun login(context: Context): String {
        if (!isConfigured) throw NotConfiguredException()
        return suspendCancellableCoroutine { cont ->
            // 카카오와 같은 이유로(콜백 중복 호출 가능성) CAS 로 완료 권한을 하나에게만 준다.
            val completed = AtomicBoolean(false)

            fun succeed(accessToken: String) {
                if (completed.compareAndSet(false, true)) cont.resume(accessToken)
            }

            fun fail(error: Throwable) {
                if (completed.compareAndSet(false, true)) cont.resumeWithException(error)
            }

            NaverIdLoginSDK.authenticate(
                context,
                object : OAuthLoginCallback {
                    override fun onSuccess() {
                        val accessToken = NaverIdLoginSDK.getAccessToken()
                        if (accessToken.isNullOrBlank()) {
                            fail(IllegalStateException("네이버 accessToken 이 비어 있습니다."))
                        } else {
                            succeed(accessToken)
                        }
                    }

                    override fun onFailure(
                        httpStatus: Int,
                        message: String,
                    ) {
                        if (NaverIdLoginSDK.getLastErrorCode().code == ERROR_CODE_USER_CANCEL) {
                            fail(CanceledException())
                        } else {
                            fail(
                                RuntimeException(
                                    "네이버 로그인 실패: $httpStatus ${NaverIdLoginSDK.getLastErrorCode().code} - " +
                                        NaverIdLoginSDK.getLastErrorDescription(),
                                ),
                            )
                        }
                    }

                    override fun onError(
                        errorCode: Int,
                        message: String,
                    ) {
                        onFailure(errorCode, message)
                    }
                },
            )
        }
    }

    /** 서버 세션과 별개인 SDK 쪽 토큰을 지운다. 앱 로그아웃 시 함께 부르면 계정 전환이 자연스럽다. */
    fun logout() {
        if (isConfigured) NaverIdLoginSDK.logout()
    }
}
