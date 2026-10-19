package com.chillsam.courmy.common.entity.category

// 서버 카테고리 코드 → 화면 라벨. 정본은 `.ai/taxonomy.md` 다.
// 변환은 표시용이며 서버로 되돌려 보내는 값에는 사용하지 않는다.

/** 장소 카테고리 코드(`cafe`, `restaurant` …) → 한글 라벨. */
fun String?.toPlaceCategoryLabel(): String {
    val raw = this?.trim().orEmpty()
    if (raw.isEmpty() || raw.equals("UNKNOWN", ignoreCase = true)) return ""
    return PlaceCategoryVO.labelOf(raw).ifBlank { raw }
}

/** 장소 카테고리 목록을 한 줄 라벨로 잇는다. 빈 값·`UNKNOWN`은 제외한다. */
fun List<String>?.toPlaceCategoryLabel(separator: String = " · "): String =
    orEmpty()
        .map { it.toPlaceCategoryLabel() }
        .filter { it.isNotBlank() }
        .joinToString(separator)

/**
 * 코스 태그 코드(`date` …)·서버 `theme` enum 이름(`DATE` …) → 한글 라벨.
 * 모르는 값은 빈 문자열이다 — 목록 카드는 빈 칩을 렌더하지 않는 계약이라 원문 fallback 은 호출부가 정한다.
 */
fun String?.toCourseTagLabel(): String = CourseTagVO.labelOf(this)
