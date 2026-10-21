package com.chillsam.courmy

import android.app.Application
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.navercorp.nid.NaverIdLoginSDK
import java.security.KeyStore

/**
 * [NaverIdLoginSDK.initialize] 는 내부에서 EncryptedSharedPreferences 를 여는데, Android Keystore 의
 * 마스터 키(`_androidx_security_master_key_`)가 깨져 있으면("exists but is unusable") 예외를 던진다.
 * 깨진 키는 저절로 복구되지 않아 **앱 시작마다 크래시가 영구 재현**된다(SCRUM-627).
 *
 * SDK 에도 자체 복구(workaround)가 있지만 그 재시도까지 실패하면 예외가 그대로 전파되므로,
 * 여기서 네이버 SDK 의 암호화 상태를 지우고 한 번 더 시도한 뒤, 그래도 실패하면 네이버 로그인만
 * 비활성화한 채 앱을 기동한다(로그인 버튼은 "준비되지 않았어요" 안내로 degradation).
 *
 * 지우는 마스터 키는 Naver SDK 전용이다. 자체 세션 토큰은 별도 alias(`courmy_session_key`)를
 * 쓰므로 영향이 없고, 복구 비용은 네이버 재로그인 1회뿐이다.
 */
@Suppress("TooGenericExceptionCaught") // 초기화 실패는 종류와 무관하게 앱 기동을 막으면 안 되는 경계다.
internal fun Application.initializeNaverSdkOrDisable(
    clientId: String,
    clientSecret: String,
    clientName: String,
    telemetry: Telemetry,
): Boolean =
    try {
        NaverIdLoginSDK.initialize(this, clientId, clientSecret, clientName)
        true
    } catch (firstFailure: Exception) {
        clearNaverCryptoState()
        try {
            NaverIdLoginSDK.initialize(this, clientId, clientSecret, clientName)
            telemetry.recordFailure(
                AppFailure(area = "login", kind = "naver_keystore_recovered", cause = firstFailure),
            )
            true
        } catch (retryFailure: Exception) {
            telemetry.recordFailure(
                AppFailure(area = "login", kind = "naver_init_disabled", cause = retryFailure),
            )
            false
        }
    }

/**
 * Naver SDK 가 만든 암호화 prefs 와 Keystore 마스터 키를 지운다.
 * 둘 다 깨진 상태일 수 있으므로 각 단계는 실패해도 다음 단계를 진행한다.
 */
private fun Application.clearNaverCryptoState() {
    runCatching { deleteSharedPreferences(NAVER_ENCRYPTED_PREFS) }
    runCatching {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }.deleteEntry(ANDROIDX_MASTER_KEY_ALIAS)
    }
}

/** Naver SDK(com.navercorp.nid.preference.EncryptedPreferences)가 쓰는 prefs 파일 이름. */
private const val NAVER_ENCRYPTED_PREFS = "NaverOAuthLoginEncryptedPreferenceData"

private const val ANDROID_KEYSTORE = "AndroidKeyStore"

/** androidx.security-crypto 기본 마스터 키 alias. 이 앱에서는 Naver SDK 만 쓴다. */
private const val ANDROIDX_MASTER_KEY_ALIAS = "_androidx_security_master_key_"
