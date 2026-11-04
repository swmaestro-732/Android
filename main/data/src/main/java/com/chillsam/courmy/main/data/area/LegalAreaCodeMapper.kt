package com.chillsam.courmy.main.data.area

import android.content.Context
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaVO
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
        private val legalAreas: List<AreaVO> by lazy {
            val source =
                context.assets
                    .open("legal_areas.json")
                    .bufferedReader()
                    .use { it.readText() }
            Json { ignoreUnknownKeys = true }
                .decodeFromString<List<LegalAreaCodeDTO>>(source)
                .map { AreaVO(it.code, it.name, it.fullName, AreaLevel.DONG) }
                .also { areas ->
                    require(areas.isNotEmpty() && areas.all { it.code.length == 10 && it.code.all(Char::isDigit) })
                }
        }

        private val legalCodes: List<String> by lazy { legalAreas.map { it.code } }

        /** 전체 범위만 합치고 일부 동 선택은 그대로 남겨 서버 저장 범위를 넓히지 않는다. */
        suspend fun restore(regions: List<AreaVO>): List<AreaVO> =
            withContext(Dispatchers.IO) {
                if (regions.isEmpty()) emptyList() else restoreLegalAreas(regions, legalAreas)
            }

        suspend fun expand(codes: List<String>): List<String> =
            withContext(Dispatchers.IO) {
                if (codes.isEmpty()) emptyList() else expandLegalAreaCodes(codes, legalCodes)
            }
    }

@Serializable
private data class LegalAreaCodeDTO(
    val code: String,
    val name: String,
    val fullName: String,
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

/** 서버의 10자리 지역 목록을 지도에서 표시할 수 있는 가장 큰 완전 포함 범위로 묶는다. */
internal fun restoreLegalAreas(
    regions: List<AreaVO>,
    legalAreas: List<AreaVO>,
): List<AreaVO> {
    val selected = mutableSetOf<String>()
    val unknown = mutableListOf<AreaVO>()
    regions.forEach { region ->
        val prefix = region.code.toAreaPrefix()
        val matches = legalAreas.filter { it.code.startsWith(prefix) }
        if (matches.isEmpty()) {
            unknown.add(region.copy(code = prefix))
        } else {
            selected.addAll(matches.map { it.code })
        }
    }
    val restored =
        legalAreas.groupBy { it.code.take(2) }.flatMap { (province, areas) ->
            restoreProvince(province, areas, selected)
        }
    return (restored + unknown).distinctBy { it.code }.sortedBy { it.code }
}

private fun restoreProvince(
    province: String,
    areas: List<AreaVO>,
    selected: Set<String>,
): List<AreaVO> {
    if (areas.all { it.code in selected }) {
        val name = areas.first().fullName.substringBefore(' ')
        return listOf(AreaVO(province, name, name, AreaLevel.SIDO))
    }
    return areas.groupBy { it.code.take(5) }.flatMap { (district, children) ->
        // 세종은 중간 시·군·구 단계 없이 바로 읍·면·동이 이어진다.
        if (province != "36" && children.all { it.code in selected }) {
            val name = children.first().fullName.substringBeforeLast(' ')
            listOf(AreaVO(district, name.substringAfter(' '), name, AreaLevel.SIGUNGU))
        } else {
            children.filter { it.code in selected }
        }
    }
}

private fun String.toAreaPrefix(): String {
    require(length in listOf(2, 5, 10) && all(Char::isDigit)) { "잘못된 관심 지역 코드" }
    return when {
        length == 10 && drop(2).all { it == '0' } -> take(2)
        length == 10 && drop(5).all { it == '0' } -> take(5)
        else -> this
    }
}
