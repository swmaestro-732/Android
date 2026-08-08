package com.chillsam.courmy.main.data.area.dto

import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaVO
import kotlinx.serialization.Serializable

/** `GET /api/v1/areas/search` 응답. 공통 봉투 `{ code, message, data }` 안에 목록이 온다. */
@Serializable
data class AreaSearchEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: List<AreaDTO>? = null,
)

/**
 * 행정구역 한 건.
 *
 * `prefix` 가 법정동코드 prefix 이며 [AreaVO.code] 로 그대로 옮긴다.
 * `level` 은 서버 enum 문자열(SIDO·SIGUNGU·DONG)이지만, 서버가 단계를 늘려도 파싱이 깨지지 않도록
 * enum 으로 직접 받지 않고 문자열로 받아 [toVO] 에서 매핑한다.
 */
@Serializable
data class AreaDTO(
    val prefix: String? = null,
    val shortName: String? = null,
    val fullName: String? = null,
    val level: String? = null,
)

/** 코드가 없으면 회원가입 요청에 실을 수 없어 쓸모가 없으므로 걸러 낸다. */
fun List<AreaDTO>.toVOList(): List<AreaVO> = mapNotNull { it.toVO() }

private fun AreaDTO.toVO(): AreaVO? {
    val code = prefix?.takeIf { it.isNotBlank() }
    // 짧은 이름이 비면 전체 이름으로 대신한다.
    val short = shortName?.takeIf { it.isNotBlank() } ?: fullName?.takeIf { it.isNotBlank() }
    // 코드가 없으면 회원가입 요청에 실을 수 없고, 이름이 없으면 칩에 그릴 게 없다.
    if (code == null || short == null) return null
    return AreaVO(
        code = code,
        shortName = short,
        fullName = fullName?.takeIf { it.isNotBlank() } ?: short,
        level =
            when (level) {
                "SIDO" -> AreaLevel.SIDO
                "SIGUNGU" -> AreaLevel.SIGUNGU
                "DONG" -> AreaLevel.DONG
                else -> AreaLevel.UNKNOWN
            },
    )
}
