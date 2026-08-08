package com.chillsam.courmy.common.data.category

// 서버 카테고리 코드 → 화면 라벨.
//
// 정본은 `.ai/taxonomy.md` 의 "장소 카테고리"·"코스 태그" 표다. 홈 피드·저장함·코스 상세·
// 장소 검색이 같은 규칙을 써야 해 data 레이어 공용 자리에 둔다.
//
// 변환은 표시용이다. 서버로 되돌려 보내는 값(코스 편집의 `tags` 등)에는 쓰지 않는다 —
// 라벨을 그대로 보내면 서버가 모르는 태그가 된다.

/**
 * 장소 카테고리 코드(`CAFE`, `RESTAURANT` …) → 한글 라벨.
 *
 * 서버가 코드 대신 이미 한글을 주는 엔드포인트(외부 지도 검색 등)도 있어, 모르는 값은
 * 지우지 않고 그대로 돌려준다. 분류가 안 된 `UNKNOWN` 만 빈 문자열로 떨어뜨려 칩을 감춘다.
 */
fun String?.toPlaceCategoryLabel(): String {
    val raw = this?.trim().orEmpty()
    if (raw.isEmpty()) return ""
    return when (raw.uppercase()) {
        "CAFE" -> "카페·디저트"
        "RESTAURANT" -> "음식점"
        "BAR" -> "술집·바"
        "SHOPPING" -> "쇼핑·상점"
        "CULTURE" -> "문화·전시"
        "EXPERIENCE" -> "체험·클래스"
        "NATURE" -> "자연·아웃도어"
        "LANDMARK" -> "역사·명소"
        "ENTERTAINMENT" -> "여가·엔터테인먼트"
        "WELLNESS" -> "웰니스·힐링"
        "UNKNOWN" -> ""
        else -> raw
    }
}

/** 장소 카테고리 목록을 한 줄 라벨로 잇는다. 빈 값·`UNKNOWN` 은 빠진다. */
fun List<String>?.toPlaceCategoryLabel(separator: String = " · "): String =
    orEmpty()
        .map { it.toPlaceCategoryLabel() }
        .filter { it.isNotBlank() }
        .joinToString(separator)

/**
 * 코스 태그 코드(`DATE`, `HEALING` …) → 한글 라벨.
 *
 * 라벨은 칩 한 줄에 들어가야 해 정본 표보다 짧게 줄여 쓴다(예: "힐링·산책" → "힐링").
 * 모르는 값(서버에 태그가 추가된 경우)이나 null 은 빈 문자열 — 호출부가 칩을 렌더하지 않는다.
 */
fun String?.toCourseTagLabel(): String =
    when (this?.trim()?.uppercase()) {
        "DATE" -> "데이트"
        "HEALING" -> "힐링"
        "FOOD" -> "맛집"
        "CAFETOUR" -> "카페투어"
        "CULTURE" -> "문화·전시"
        "NATURE" -> "자연"
        "NIGHTVIEW" -> "야경"
        "SHOPPING" -> "쇼핑"
        "TRADITION" -> "전통"
        "ACTIVITY" -> "액티비티"
        "FAMILY" -> "가족"
        "SOLO" -> "혼자"
        else -> ""
    }
