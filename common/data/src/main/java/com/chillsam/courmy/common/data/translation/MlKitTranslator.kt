package com.chillsam.courmy.common.data.translation

import com.chillsam.courmy.common.domain.translation.AppLanguage
import com.chillsam.courmy.common.domain.translation.Translator
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.google.mlkit.nl.translate.Translator as MlKitClient

/**
 * Google ML Kit on-device Translation 구현. 원문은 항상 한국어로 보고 선택 언어로 번역한다.
 *
 * 언어팩은 첫 사용 시 다운로드되고 이후 로컬 캐시된다. 클라이언트는 언어별로 한 번 만들어 재사용한다
 * (매 호출 생성/해제 비용 제거). @Singleton 이라 프로세스 생명주기 동안 유지한다.
 */
@Singleton
class MlKitTranslator
    @Inject
    constructor() : Translator {
        private val clients = mutableMapOf<AppLanguage, MlKitClient>()
        private val mutex = Mutex()

        override suspend fun translate(
            text: String,
            target: AppLanguage,
        ): String {
            if (target == AppLanguage.KOREAN || text.isBlank()) return text
            val client = mutex.withLock { clients.getOrPut(target) { createClient(target) } }
            client.downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
            return client.translate(text).await()
        }

        private fun createClient(target: AppLanguage): MlKitClient {
            val options =
                TranslatorOptions
                    .Builder()
                    .setSourceLanguage(TranslateLanguage.KOREAN)
                    .setTargetLanguage(target.toMlKitLanguage())
                    .build()
            return Translation.getClient(options)
        }

        private fun AppLanguage.toMlKitLanguage(): String =
            when (this) {
                AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH

                AppLanguage.JAPANESE -> TranslateLanguage.JAPANESE

                // 한국어는 번역 대상이 아니라 위에서 걸러진다. 방어적으로 영어로 둔다.
                AppLanguage.KOREAN -> TranslateLanguage.ENGLISH
            }
    }

/** GMS [Task] 를 코루틴으로 기다린다(play-services-tasks 코루틴 의존성 없이 최소 래핑). */
private suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { continuation.resume(it) }
        addOnFailureListener { continuation.resumeWithException(it) }
    }
