package com.chillsam.courmy.main.data.area

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/** 화면의 시·도/시·군·구 선택을 서버가 받는 활성 법정 읍·면·동 코드 배열로 변환한다. */
@Singleton
class LegalAreaCodeMapper
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val legalCodes: List<String> by lazy {
            val source =
                context.assets
                    .open("legal_areas.json")
                    .bufferedReader()
                    .use { it.readText() }
            Json { ignoreUnknownKeys = true }
                .decodeFromString<List<LegalAreaCodeDTO>>(source)
                .map { it.code }
                .also { codes ->
                    require(codes.isNotEmpty() && codes.all { it.length == 10 && it.all(Char::isDigit) })
                }
        }

        suspend fun expand(codes: List<String>): List<String> =
            withContext(Dispatchers.IO) {
                if (codes.isEmpty()) emptyList() else expandLegalAreaCodes(codes, legalCodes)
            }
    }

@Serializable
private data class LegalAreaCodeDTO(
    val code: String,
)

/** 알 수 없는 선택을 버리고 일부만 저장하지 않도록 각 입력을 검증한다. */
internal fun expandLegalAreaCodes(
    selectedCodes: List<String>,
    legalCodes: List<String>,
): List<String> =
    selectedCodes
        .flatMap { code ->
            require(code.length in listOf(2, 5, 10) && code.all(Char::isDigit)) { "잘못된 지역 코드: $code" }
            val prefix =
                when {
                    code.length == 10 && code.drop(2).all { it == '0' } -> code.take(2)
                    code.length == 10 && code.drop(5).all { it == '0' } -> code.take(5)
                    else -> code
                }
            legalCodes.filter { it.startsWith(prefix) }.also {
                require(it.isNotEmpty()) { "존재하지 않는 지역 코드: $code" }
            }
        }.distinct()
        .sorted()
