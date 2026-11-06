package com.chillsam.courmy.main.entity.search

/**
 * 코스 검색 정렬. [name] 이 서버 `sort` 파라미터 값 그대로다(RELEVANCE·LATEST·POPULAR, 2026-11-07 실호출 확인).
 * 기본값은 서버와 같은 [RELEVANCE] — 이때는 파라미터를 싣지 않는다.
 */
enum class SearchCourseSortVO(
    val label: String,
) {
    RELEVANCE("관련순"),
    LATEST("최신순"),
    POPULAR("인기순"),
}
