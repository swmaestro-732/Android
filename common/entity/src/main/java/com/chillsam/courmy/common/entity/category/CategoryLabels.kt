package com.chillsam.courmy.common.entity.category

// 서버 카테고리 코드 → 화면 라벨. 정본은 `.ai/taxonomy.md` 다.
// 변환은 표시용이며 서버로 되돌려 보내는 값에는 사용하지 않는다.

/** 장소 카테고리 코드(`CAFE`, `RESTAURANT` …) → 한글 라벨. */
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

/** 장소 카테고리 목록을 한 줄 라벨로 잇는다. 빈 값·`UNKNOWN`은 제외한다. */
fun List<String>?.toPlaceCategoryLabel(separator: String = " · "): String =
    orEmpty()
        .map { it.toPlaceCategoryLabel() }
        .filter { it.isNotBlank() }
        .joinToString(separator)

/** 코스 태그 코드를 [CourseCategoryVO]의 한글 라벨로 바꾼다. */
fun String?.toCourseTagLabel(): String = CourseCategoryVO.labelOf(this)
