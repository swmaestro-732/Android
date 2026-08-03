package com.chillsam.courmy.common.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_session")

/**
 * 세션 토큰(access/refresh)을 Android Keystore(AES/GCM)로 암호화해 DataStore 에 보관한다.
 * 평문 저장 금지 규약(../.ai android/architecture/data-layer.md)에 따라 키는 Keystore 안에만 있고,
 * DataStore 에는 암호문만 남는다.
 *
 * 앱 재설치·기기 복원 등으로 Keystore 키가 사라지면 복호화가 실패하는데, 그때는 예외를 던지지 않고
 * null 을 돌려 "세션 없음(재로그인)"으로 흘려보낸다. 토큰 외 사용자 데이터는 서버에 있어 손실이 없다.
 */
@Singleton
class SecureTokenStorage
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        data class Tokens(
            val accessToken: String,
            val refreshToken: String,
        )

        /** 저장된 세션을 복호화해 돌려준다. 값이 없거나 복호화 실패면 null. */
        suspend fun read(): Tokens? {
            val prefs = context.sessionDataStore.data.first()
            val access = prefs[KEY_ACCESS]?.let(::decryptOrNull)
            val refresh = prefs[KEY_REFRESH]?.let(::decryptOrNull)
            return if (access != null && refresh != null) Tokens(access, refresh) else null
        }

        suspend fun write(
            accessToken: String,
            refreshToken: String,
        ) {
            val encAccess = encrypt(accessToken)
            val encRefresh = encrypt(refreshToken)
            context.sessionDataStore.edit {
                it[KEY_ACCESS] = encAccess
                it[KEY_REFRESH] = encRefresh
            }
        }

        suspend fun clear() {
            context.sessionDataStore.edit { it.clear() }
        }

        private fun secretKey(): SecretKey {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            generator.init(
                KeyGenParameterSpec
                    .Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build(),
            )
            return generator.generateKey()
        }

        private fun encrypt(plain: String): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey())
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
            // IV(12B) + 암호문을 이어붙여 Base64 로 보관한다(복호화 때 앞 12바이트가 IV).
            val out = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, out, 0, iv.size)
            System.arraycopy(cipherText, 0, out, iv.size, cipherText.size)
            return Base64.encodeToString(out, Base64.NO_WRAP)
        }

        private fun decryptOrNull(blob: String): String? =
            runCatching {
                val bytes = Base64.decode(blob, Base64.NO_WRAP)
                val iv = bytes.copyOfRange(0, IV_SIZE)
                val cipherText = bytes.copyOfRange(IV_SIZE, bytes.size)
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_BITS, iv))
                String(cipher.doFinal(cipherText), Charsets.UTF_8)
            }.getOrNull()

        private companion object {
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val KEY_ALIAS = "courmy_session_key"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val IV_SIZE = 12
            const val TAG_BITS = 128
            val KEY_ACCESS = stringPreferencesKey("enc_access_token")
            val KEY_REFRESH = stringPreferencesKey("enc_refresh_token")
        }
    }
