package com.chillsam.courmy.main.data.course

/**
 * 서버 `CourseCategory` enum 이름 → 화면 라벨.
 *
 * 홈 피드·저장함 등 코스 요약을 그리는 화면이 같은 규칙을 써야 해 한 곳에 둔다.
 * 모르는 값(서버에 카테고리가 추가된 경우)이나 null 은 빈 문자열 — 호출부가 칩을 렌더하지 않는다.
 */
internal fun String?.toCourseCategoryLabel(): String =
    when (this) {
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
