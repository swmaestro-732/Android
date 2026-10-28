package com.chillsam.courmy.main.entity.area

import kotlinx.serialization.Serializable

/**
 * 행정구역 한 건(`GET /api/v1/areas/search`).
 *
 * [code] 는 법정동코드 prefix 로, 회원가입 요청(`SignupRequest.areaCodes`)에 그대로 싣는 값이다.
 * 자릿수가 곧 단계라 [level] 과 짝을 이룬다 — 시도 2 / 시군구 5 / 읍면동 10자리.
 *
 * 관심 지역을 기기에 보관할 때 그대로 직렬화하므로 [Serializable] 이다.
 */
@Serializable
data class AreaVO(
    val code: String,
    /** 칩에 쓰는 짧은 이름(예: "성수동1가"). */
    val shortName: String,
    /** 검색 결과 줄에 쓰는 전체 이름(예: "서울특별시 성동구 성수동1가"). */
    val fullName: String,
    val level: AreaLevel = AreaLevel.UNKNOWN,
)

/** 행정구역 단계. 서버 enum(SIDO·SIGUNGU·DONG) 밖의 값은 [UNKNOWN] 으로 떨어뜨린다. */
@Serializable
enum class AreaLevel {
    SIDO,
    SIGUNGU,
    DONG,
    UNKNOWN,
}
